package com.cmatch.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * {@link ProductMutationDTO}의 {@link ImageExtension} 애노테이션이 붙은
 * {@link MultipartFile} 타입 프로퍼티에 대하여 올바른(지원하는) 이미지 확장자 인지를 검증한다.
 * 
 * 
 * @author leeseunghyun
 *
 */
@Slf4j
@Component
public class ImageExtensionValidator implements ConstraintValidator<ImageExtension, MultipartFile> {

    private static final List<String> supportedExtensions = new ArrayList<String>() {

        private static final long serialVersionUID = -6913419777268438554L;

        {
            add(".jpg");
            add(".JPG");
            add(".jpeg");
            add(".JPEG");
            add(".png");
            add(".PNG");
            add(".tif");
            add(".tiff");
        }
    };

    @Autowired
    private MessageSource msgSource;

    @Override
    public void initialize(ImageExtension contactNumber) {
    }

    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {

        log.debug("Starting MultipartFile validation.");

        if (Objects.isNull(file) || file.isEmpty()) {
            log.info("Empty multipart file detected in {}.", this);

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(
                    msgSource.getMessage("response.exception.multipart.empty", null, LocaleContextHolder.getLocale()))
                    .addConstraintViolation();

            return false;
        }

        log.debug("Starting to validate image extendsion from file name.");

        String filename = file.getOriginalFilename();

        if (StringUtils.isEmpty(file.getOriginalFilename())) {
            log.info("Received empty String type argument filename while trying to validate image.");
            throw new IllegalArgumentException("Empty String argument detected.");
        }

        int extensionIdx = filename.lastIndexOf(".");
        if (extensionIdx == -1) {
            log.debug("Received filename doesn't have extensions. filename : {}", filename);
            return false;
        }

        String extension = filename.substring(extensionIdx, filename.length());

        log.debug("Received file extension : {}", extension);

        if (supportedExtensions.stream().anyMatch(supported -> supported.equals(extension))) {
            log.debug("Image file extension validation succeeded. ");

            return true;
        } else {
            log.debug("Image file extension validation failed. Invalid file extendsion : {}", extension);

            return false;
        }
    }
}
