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
                public static final String find =   "queue.stream.rpc.find";
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
                public static final String find =   "exchanger.stream.rpc.find";
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
                public static final String find =   "binding.stream.rpc.find";
            }
        }
    }

}
