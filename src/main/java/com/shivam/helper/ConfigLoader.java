package com.shivam.helper;

import com.shivam.config.DefaultConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

public class ConfigLoader {

    private static ConfigLoader c;
    private ConfigLoader(){};

    private static final Logger log = LoggerFactory.getLogger(ConfigLoader.class);

    public static synchronized ConfigLoader getConfigLoader(){
        if(c == null){
            c = new ConfigLoader();
        }
        return c;
    }

    public void validateAndApplyConfigurations(String[] args, DefaultConfig config){
        // if args are present then validate and apply args
            Map<String,Object> argMap = Arrays.stream(args).filter((a -> a.contains("--")))
                    .map(a -> a.substring(2).split("=",2))
                    .filter(a -> a.length == 2)
                    .collect(Collectors.toMap(
                            a -> a[0].toLowerCase(),
                            a -> a[1],
                            (oldVal,newVal) -> newVal
                    ));
            if(setAndValidate(argMap,config)){
                log.info("Config is successfully loaded from the arguments");
                return;
            }else if(loadAndValidateFromFileSystem(config))// check the config file in the file system and validate and apply it
            {
                log.info("Config is successfully loaded from FILESYSTEM");
            }else // apply default config
            {
                try{
                    loadDefaultConfig(config);
                }catch (Exception e){
                    log.error("Error while setting the config, no config was set by the system");
                    e.printStackTrace();
                }
                log.info("Default configuration is applied");
            }
    }

    private void loadDefaultConfig(DefaultConfig config) throws IOException {
        Properties props = new Properties();
        try(InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")){
            if(is == null) {
                throw new RuntimeException("Application properties not found");
            }

            props.load(is);
            config.setMaxFailedAttempts(Integer.parseInt(props.getProperty("maxFailedAttempts")));
            config.setBanDurationMinuites(Integer.parseInt(props.getProperty("banDurationMinuites")));
            config.setTimeWindowSeconds(Integer.parseInt(props.getProperty("timeWindowSeconds")));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private boolean loadAndValidateFromFileSystem(DefaultConfig config){
        Map<String,Object> configFromFile = loadFromFileSystem();
        System.out.println("config from file "+ configFromFile);
        if(configFromFile != null){
            return setAndValidate(configFromFile,config);
        }else{
            log.error("config.yml is present but the fields are not correct");
            return false;
        }
    }

    private Map<String,Object> loadFromFileSystem(){
        // check if .yml file exists in file system
        Path path = Paths.get("/etc/ssh-protector/config.yml");
//        Path path = Paths.get("D:\\ssh-prot-fileTest/config.yml");
        if(!Files.exists(path)){
            return null;
        }
        Map<String,Object> result = null;
        try(InputStream is = Files.newInputStream(path)){
            Yaml yaml = new Yaml();
            result = yaml.load(is);

            // convert the keys present into lowercase
            return result.entrySet().stream().collect(Collectors.toMap(
                    entry -> entry.getKey().toLowerCase(),
                    entry -> (Object)entry.getValue(),
                    (oldVal,newVal) -> oldVal
            ));
        }catch(IOException io){
            log.error("Error while loading the configuration from the fileSystem ");
            io.printStackTrace();
            return null;
        }
    }

    // this function checks the input values provided via map and maps to the config object, the map will have Object as value
    // so different use cases must be handled correctly for future requirement.
    private boolean setAndValidate(Map<String,Object> map,DefaultConfig config){
        Class<?> clazz = config.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for(Field f : fields){
            if(map.containsKey(f.getName().toLowerCase())){
                String fieldName = f.getName();
                String setterFieldName = "set"+Character.toUpperCase(fieldName.charAt(0))+fieldName.substring(1);
                Method setter = null;
                try{
                    setter = clazz.getMethod(setterFieldName,f.getType());
                    setter.setAccessible(true);
                    Type[] paramterType = setter.getGenericParameterTypes();
                    if(paramterType.length > 0){
                        if(paramterType[0] == int.class){
                            Object value = map.get(f.getName().toLowerCase());
                            // here the object value can be either String or an Integer so handel it correctly for both the cases
                            if (value instanceof Integer) {
                                 setter.invoke(config,(Integer) value);
                            }
                            if (value instanceof String) {
                                setter.invoke(config,Integer.parseInt((String) value));
                            }
                        }
                    }
                }catch(NoSuchMethodException n){
                    log.error("No Setter method found for setter {} ",setter);
                    n.printStackTrace();
                }catch(InvocationTargetException i){
                    i.printStackTrace();
                }catch (IllegalAccessException a){
                    log.error("No permission to access the method {} ",setter);
                    a.printStackTrace();
                }

            }
        }
        // after the fields have been set, check if all the mandatory fields are present or not
        return config.checkMandatoryFieldsSet();
    }
}
