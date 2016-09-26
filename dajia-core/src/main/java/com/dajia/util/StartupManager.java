package com.dajia.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * Created by huhaonan on 2016/9/27.
 */

@Component
public class StartupManager implements InitializingBean {

    final static Logger logger = LoggerFactory.getLogger(StartupManager.class);

    public static void printLogo() {
        String logo = "___           ___            ___                     ___     \n" +
                "     /\\  \\         /\\  \\          /\\  \\        ___        /\\  \\    \n" +
                "    /::\\  \\       /::\\  \\         \\:\\  \\      /\\  \\      /::\\  \\   \n" +
                "   /:/\\:\\  \\     /:/\\:\\  \\    ___ /::\\__\\     \\:\\  \\    /:/\\:\\  \\  \n" +
                "  /:/  \\:\\__\\   /::\\~\\:\\  \\  /\\  /:/\\/__/     /::\\__\\  /::\\~\\:\\  \\ \n" +
                " /:/__/ \\:|__| /:/\\:\\ \\:\\__\\ \\:\\/:/  /     __/:/\\/__/ /:/\\:\\ \\:\\__\\\n" +
                " \\:\\  \\ /:/  / \\/__\\:\\/:/  /  \\::/  /     /\\/:/  /    \\/__\\:\\/:/  /\n" +
                "  \\:\\  /:/  /       \\::/  /    \\/__/      \\::/__/          \\::/  / \n" +
                "   \\:\\/:/  /        /:/  /                 \\:\\__\\          /:/  /  \n" +
                "    \\::/__/        /:/  /                   \\/__/         /:/  /   \n" +
                "     ~~            \\/__/                                  \\/__/    ";

        logger.info(logo);

        logger.info("打价中文测试");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        printLogo();
    }

    public static void main(String[] args) {
        printLogo();
    }
}
