package ru.kvanttelecom.tv.streammonitoring.utils.dto.constants;

public class ControllersPaths {

    public static class monitor {
        public static class stream {
            public static final String all = "/stream/all";
            public static final String idList = "/stream/findById";
        }
    }

    public static class tbot {
        public static class stream {
            public static final String update = "/stream/update";
        }
    }
}
