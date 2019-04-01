package springbook.learningtest.factorybean;

import org.springframework.beans.factory.FactoryBean;

public class Message {
    String text;

    private Message(String text){
        this.text=text;
    }

    public static Message newMessage(String text){
        return new Message(text);
    }

    public String getText(){
        return text;
    }


    public void setText(String text){
        this.text=text;
    }
}
