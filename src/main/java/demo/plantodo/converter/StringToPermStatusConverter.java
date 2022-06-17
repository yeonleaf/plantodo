package demo.plantodo.converter;

import demo.plantodo.domain.PermStatus;
import org.springframework.core.convert.converter.Converter;

public class StringToPermStatusConverter implements Converter<String, PermStatus> {
    @Override
    public PermStatus convert(String source) {
        return source.equals("granted") ? PermStatus.GRANTED : PermStatus.DENIED;
    }
}
