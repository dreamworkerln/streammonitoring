package ru.kvanttelecom.tv.streammonitoring.core.dto.stream;

import lombok.Data;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.dto._base.AbstractDto;

@Data
public class StreamDto extends AbstractDto {

    //private StreamKeyDto streamKey;


    private String name;

    private String title;

    private String hostname;

    private String comment;

    private String postalAddress;
    private String coordinates;

    private String client;

    /**
     * Is stream alive
     */
    private boolean alive;

    /**
     * Is stream flapping
     */
    private boolean flapping;

    public StreamDto() {}

}
