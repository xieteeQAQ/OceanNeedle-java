package com.xietee.ui;

public class AnsiColors {

        // 重置所有属性
        public static final String RESET = "\u001B[0m";

        // 常规颜色
        public static final String GREEN = "\u001B[32m";
        public static final String YELLOW = "\u001B[33m";
        public static final String BLUE = "\u001B[34m";
        public static final String CYAN = "\u001B[36m";

        // 亮色
        public static final String BRIGHT_BLACK = "\u001B[90m";
        public static final String BRIGHT_RED = "\u001B[91m";
        public static final String BRIGHT_GREEN = "\u001B[92m";
        public static final String BRIGHT_YELLOW = "\u001B[93m";
        public static final String BRIGHT_BLUE = "\u001B[94m";
        public static final String BRIGHT_CYAN = "\u001B[96m";
        public static final String BRIGHT_WHITE = "\u001B[97m";

        // 底色 命名规则: <text_color>__<background_color>
        public static final String BLACK__WHITE = "\033[30;47m";
        public static final String BLACK__BRIGHT_RED = "\033[30;101m";
        public static final String BLACK__BRIGHT_YELLOW = "\033[30;103m";
        public static final String BLACK__BRIGHT_GREEN = "\033[30;102m";
}
