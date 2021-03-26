package ru.kvanttelecom.tv.streammonitoring.utils.dto;

import lombok.Data;

@Data
public class StreamDto {

        /**
         * Server id
         */
        private int serverId;


        /**
         * Stream name
         */
        private String name;

        /**
         * Stream title
         */
        private String title;

        /**
         * Is stream alive
         */
        private boolean alive;

        public StreamDto() {}

        public StreamDto(String name, String title, boolean alive) {
                this.name = name;
                this.title = title;
                this.alive = alive;
        }
}
