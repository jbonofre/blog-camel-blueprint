package net.nanthrax.blog.service.internal;

import net.nanthrax.blog.service.EchoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EchoServiceImpl implements EchoService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EchoServiceImpl.class);

    public String echo(String message) {
        return "Echoing " + message;
    }

}
