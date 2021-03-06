package com.cmatch.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cmatch.dto.MatchingCriteria;
import com.cmatch.stage.InstantChatTimeLimit;
import com.cmatch.stage.StageUnsubscribeInterceptor;
import com.cmatch.support.code.CodeBook;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class AppController {
    
    // Static Fields
    // ==========================================================================================================================

    public static final String IGNORABLE_MESSAGE_HANDLER_END_POINT = "/customMessage";
    
    // Instance Fields
    // ==========================================================================================================================

    private final CodeBook codeBook;

    @Value("${instantChat.timeInterval.sec}")
    private long instantChatInterval;
    @Value("${followingChoice.timeInterval.sec}")
    private long followingChoiceInterval;

    // Constructors
    // ==========================================================================================================================

    public AppController(CodeBook codeBook) {
        this.codeBook = codeBook;
    }

    // Methods
    // ==========================================================================================================================

    @GetMapping("/")
    public String index(Model model) {

        log.info("Index page request reached.");

        model.addAttribute("codeBook", codeBook);
        model.addAttribute("matchingCriteria", new MatchingCriteria());

        return "index";
    }

    @ResponseBody
    @GetMapping("/code")
    public ResponseEntity<CodeBook> getCodeBook() {
                
        return new ResponseEntity<>(codeBook, HttpStatus.OK);
    }

    @GetMapping("/instantChat/timeLimit")
    @ResponseBody
    public ResponseEntity<InstantChatTimeLimit> getTimeLimitProperty() {
        
        return new ResponseEntity<InstantChatTimeLimit>(
                new InstantChatTimeLimit(instantChatInterval, followingChoiceInterval), HttpStatus.OK);
    }

    /**
     * {@link StageUnsubscribeInterceptor}
     */
    @MessageMapping(IGNORABLE_MESSAGE_HANDLER_END_POINT)
    public void handleIgnorableMessage() {
        
    }
}
