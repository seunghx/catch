package com.cmatch.support.code;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.stereotype.Component;

@SuppressWarnings("rawtypes")
@Component
public class EnumConverter implements ConverterFactory<String, Enum> {

    // Static Fields
    // ==========================================================================================================================

    private static class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

        private Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        @Override
        public T convert(String source) {
            @SuppressWarnings("unchecked")
            T enumValue = (T) Enum.valueOf(this.enumType, source.trim());
            return enumValue;
        }
    }
    
    // Methods
    // ==========================================================================================================================
    
    @Override
    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {

        @SuppressWarnings("unchecked")
        Converter<String, T> customConverter = new StringToEnumConverter(targetType);

        return customConverter;
    }

}
