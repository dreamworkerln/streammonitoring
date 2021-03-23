package ru.kvanttelecom.tv.streammonitoring.utils.dto;

import lombok.Data;

@Data
public class CameraDto {

        /**
         * Camera name
         */
        private String name;

        /**
         * Camera title
         */
        private String title;

        /**
         * Is camera alive
         */
        private boolean alive;

        public CameraDto() {}

        public CameraDto(String name, String title, boolean alive) {
                this.name = name;
                this.title = title;
                this.alive = alive;
        }
}
