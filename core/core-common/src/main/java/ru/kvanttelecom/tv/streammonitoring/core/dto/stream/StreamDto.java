package ru.kvanttelecom.tv.streammonitoring.core.dto.stream;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.kvanttelecom.tv.streammonitoring.core.data.StreamKey;
import ru.kvanttelecom.tv.streammonitoring.core.dto._base.AbstractDto;

import javax.validation.constraints.NotNull;

import static ru.dreamworkerln.spring.utils.common.StringUtils.isBlank;

@EqualsAndHashCode(callSuper = true)
@Data
public class StreamDto extends AbstractDto {

    private StreamKey streamKey;

    @NotNull                   
    private String hostname;

    @NotNull
    private String name;

    private String title;



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

    @JsonIgnore
    public String getFriendlyTitle() {
        String result = title;
        if(isBlank(title)) {
            result = name + "   (" + hostname + ")";
        }
        return result;
    }

}
