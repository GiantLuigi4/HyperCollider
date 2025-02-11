package tfc.hypercollider;

import me.jellysquid.mods.lithium.common.config.LithiumConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class Cfg {
    Properties properties = new Properties();

    public Cfg() {
        File fl = new File("./config/hypercollider.properties");
        try {
            if (!fl.exists()) {
                fl.getParentFile().mkdirs(); // ensure dir exists
                fl.createNewFile();

                FileOutputStream os = new FileOutputStream(fl);
                os.write(Cfg.class.getClassLoader().getResourceAsStream("default_props.properties").readAllBytes());
                os.flush();
                os.close();
            }
            FileInputStream fis = new FileInputStream(fl);
            properties.load(fis);
        } catch (Throwable ignored) {
        }
    }

    public boolean shouldApply(String mixinClassName, String targetClassName) {
        mixinClassName = mixinClassName.substring("tfc.hypercollider.".length());
        if (properties.containsKey(mixinClassName)) {
            return properties.getProperty(mixinClassName).equals("true");
        }
        return true;
    }
}
