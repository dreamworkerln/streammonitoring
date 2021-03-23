package ru.kvanttelecom.tv.streammonitoring.utils.converter;

import org.mapstruct.Mapper;
import ru.kvanttelecom.tv.streammonitoring.utils.data.Stream;
import ru.kvanttelecom.tv.streammonitoring.utils.data.CameraUpdate;

import javax.annotation.PostConstruct;

@Mapper
public abstract class CameraUpdateConverter {

    @PostConstruct
    private void postConstruct() {}

    //public abstract CameraUpdate toUpdate(Camera camera);
    public abstract Stream toCamera(CameraUpdate update);
}
