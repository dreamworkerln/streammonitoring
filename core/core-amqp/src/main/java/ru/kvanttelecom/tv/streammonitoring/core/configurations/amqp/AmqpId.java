package ru.kvanttelecom.tv.streammonitoring.core.configurations.amqp;

/**
 * AMQP queues, exchangers, binging Beans identifiers
 */
public class AmqpId {

    public static class queue {

        // Streams
        public static class stream {

            // Events
            public static class events {
                public static final String update = "queue.stream.events.update";
            }

            // RPC
            public static class rpc {
                public static final String findAll =    "queue.stream.rpc.findAll";
                public static final String findByKeys = "queue.stream.rpc.findByKeys";
            }
        }
    }

    public static class exchanger {
        // Streams
        public static class stream {

            // Events
            public static class events {
                public static final String update = "exchanger.stream.events.update";
            }


            // RPC
            public static class rpc {
                public static final String findAll =    "exchanger.stream.rpc.findAll";
                public static final String findByKeys = "exchanger.stream.rpc.findByKeys";
            }
        }
    }


    // Streams
    public static class binding {

        public static class stream {

            // events
            public static class events {
                public static final String update = "binding.stream.events.update";
            }

            // RPC
            public static class rpc {
                public static final String findAll =    "binding.stream.rpc.findAll";
                public static final String findByKeys = "binding.stream.rpc.findByKeys";
            }
        }
    }

}
