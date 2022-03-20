package com.patriot.bot.listeners;

import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.DiscordApi;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.event.Event;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigListener implements MessageCreateListener {
    Locale l = new Locale("ru-RU");
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if(event.getMessageContent().startsWith("патриот роль хохла")){
            Pattern p = Pattern.compile("патриот роль хохла([\\s\\d*]*)");
            Matcher m = p.matcher(event.getMessageContent());
            Pattern pp = Pattern.compile("патриот роль хохла-([\\s\\d*]*)");
            Matcher mm = pp.matcher(event.getMessageContent());
            List<Role> roles = event.getMessage().getMentionedRoles();
            if(!roles.isEmpty()){
                if(roles.size()==1){
                    EmbedBuilder eb = new EmbedBuilder().setTitle("Роль хохла").setColor(Color.green);
                    Role role = getXoxolRole(event);
                        try {
                            event.getMessage().addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));
                            if(getXoxolRole(event)==null) {eb.setDescription("Добавлена роль хохла "+roles.get(0).getMentionTag());}
                            else {eb.setDescription("Изменена роль хохла с "+getXoxolRole(event).getMentionTag()+" на "+roles.get(0).getMentionTag());}
                            addXoxolRole(roles.get(0), event);
                            Message msg = event.getChannel().sendMessage(eb).get();
                            try {
                                TimeUnit.SECONDS.sleep(5);
                                msg.delete();
                            } catch (InterruptedException e){e.printStackTrace();}
                        }catch (InterruptedException | ExecutionException e){e.printStackTrace();}
                } else {
                    event.getMessage().addReaction(EmojiParser.parseToUnicode(":x:"));
                    event.getChannel().sendMessage(
                            new EmbedBuilder()
                                    .setTitle("Ошибка")
                                    .setDescription("Может быть только одна роль хохла")
                                    .setColor(Color.red)
                    );
                }
            }else if(m.matches()){
                String[] roleIds = event.getMessageContent().substring(m.start(1), m.end(1)).split("\\s");
                if (roleIds.length==1){
                    if(!roleIds[0].trim().isEmpty()){
                        EmbedBuilder eb = new EmbedBuilder().setTitle("Роль хохла").setColor(Color.green);
                        Role role = event.getServer().get().getRoleById(roleIds[0]).get();

                            try{
                                event.getMessage().addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));
                                if(getXoxolRole(event)==null) eb.setDescription("Добавлена роль хохла "+role.getMentionTag());
                                else eb.setDescription("Изменена роль хохла с "+getXoxolRole(event).getMentionTag()+" на "+role.getMentionTag());
                                addXoxolRole(role, event);
                                Message msg = event.getChannel().sendMessage(eb).get();
                                try {
                                    TimeUnit.SECONDS.sleep(5);
                                    msg.delete();
                                }catch (InterruptedException e){e.printStackTrace();}
                            }catch (InterruptedException | ExecutionException e){e.printStackTrace();}

                    }else {
                        if (getXoxolRole(event)!=null){
                            try {
                                event.getMessage().addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));
                                Message msg = event.getChannel().sendMessage(
                                        new EmbedBuilder()
                                                .setTitle("Роль хохла")
                                                .setColor(Color.green)
                                                .setDescription(getXoxolRole(event).getMentionTag())
                                ).get();
                                try{
                                    TimeUnit.SECONDS.sleep(5);
                                    msg.delete();
                                }catch (InterruptedException e){e.printStackTrace();}
                            }catch (InterruptedException|ExecutionException e){e.printStackTrace();}
                        }else {
                            try {
                                event.getMessage().addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));
                                Message msg = event.getChannel().sendMessage(
                                        new EmbedBuilder()
                                                .setTitle("Роль хохла")
                                                .setColor(Color.green)
                                                .setDescription("Нет роли хохла")
                                ).get();
                                try{
                                    TimeUnit.SECONDS.sleep(5);
                                    msg.delete();
                                }catch (InterruptedException e){e.printStackTrace();}
                            }catch (InterruptedException|ExecutionException e){e.printStackTrace();}
                        }
                    }
                }else {
                    event.getMessage().addReaction(EmojiParser.parseToUnicode(":x:"));
                    try {
                        Message msg = event.getChannel().sendMessage(new EmbedBuilder()
                                .setTitle("Ошибка")
                                .setDescription("Может быть только одна роль хохла")
                                .setColor(Color.red)).get();
                        try {
                            TimeUnit.SECONDS.sleep(5);
                            msg.delete();
                        }catch (InterruptedException e){e.printStackTrace();}
                    }catch (InterruptedException|ExecutionException e){e.printStackTrace();}
                }
            } else if (mm.matches()){
                String[] roleIds = event.getMessageContent().substring(mm.start(1), mm.end(1)).split("\\s");
                EmbedBuilder emb = new EmbedBuilder().setTitle("Роль хохла").setColor(Color.green).setDescription("Успешно удалена роль хохла");
                if (roleIds.length==1){
                    if (!roleIds[1].isEmpty()){
                        if(getXoxolRole(event)!=null) {
                            event.getMessage().addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));
                            try {
                                Message msg = event.getChannel().sendMessage(emb).get();
                                try {
                                    TimeUnit.SECONDS.sleep(5);
                                    msg.delete();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
                            }
                        }else {
                            event.getMessage().addReaction(EmojiParser.parseToUnicode(":x:"));
                            try {
                                Message msg = event.getChannel().sendMessage(emb.setTitle("Ошибка").setDescription("Роли хохла не существует").setColor(Color.red)).get();
                                try {
                                    TimeUnit.SECONDS.sleep(5);
                                    msg.delete();
                                }catch (InterruptedException e){e.printStackTrace();}
                            } catch (InterruptedException|ExecutionException e){e.printStackTrace();}
                        }
                    }else{
                        event.getMessage().addReaction(EmojiParser.parseToUnicode(":x:"));
                        try {
                            Message msg = event.getChannel().sendMessage(emb.setTitle("Ошибка")
                                            .setDescription("Неверный синтаксис")
                                            .addField("Примеры:",
                                                    "патриот роль хохла- @рольХохла\n" +
                                                            "патриот роль хохла- 123456789123456")
                                            .setColor(Color.red)
                                    ).get();
                            try {
                                TimeUnit.SECONDS.sleep(5);
                                msg.delete();
                            }catch (InterruptedException e){e.printStackTrace();}
                        }catch (InterruptedException|ExecutionException e){e.printStackTrace();}
                    }
                }else{
                    event.getMessage().addReaction(EmojiParser.parseToUnicode(":x:"));
                    try {
                        Message msg = event.getChannel().sendMessage(emb
                                .setColor(Color.red)
                                .setTitle("Ошибка")
                                .setDescription("Может быть только 1-а роль")
                        ).get();
                        try {
                            TimeUnit.SECONDS.sleep(5);
                            msg.delete();
                        }catch (InterruptedException e){e.printStackTrace();}
                    }catch (InterruptedException|ExecutionException e){e.printStackTrace();}
                }
            }
        }
    }
    public void addXoxolRole (Role role, MessageCreateEvent event){
        Properties props = new Properties();
        InputStream is = getClass().getResourceAsStream("/config");
        try {
            props.load(is);
            FileWriter fw = new FileWriter(new File(getClass().getResource("/config").toURI()));
            props.setProperty(event.getServer().get().getIdAsString(), role.getIdAsString());
            System.out.println(event.getServer().get().getIdAsString()+role.getIdAsString());
            props.store(fw, null);
        }catch (IOException | URISyntaxException e){e.printStackTrace();}
    }
    public Role getXoxolRole (MessageCreateEvent event){
        Role role = null;
        try {
            InputStream is = getClass().getResourceAsStream("/config");
            Properties props = new Properties();
            props.load(is);
            String xoxolRole = props.getProperty(event.getServer().get().getIdAsString());
            if(xoxolRole!=null) role = event.getServer().get().getRoleById(xoxolRole).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return role;
    }
    public void deleteXoxolRole (MessageCreateEvent event){
        if(getXoxolRole(event)!=null){
            InputStream is = getClass().getResourceAsStream("/config");
            Properties props = new Properties();
            try {
                props.load(is);
                props.remove(event.getServer().get().getIdAsString());
                FileWriter fw = new FileWriter(new File(getClass().getResource("/config").toURI()));
                props.store(fw, null);
            } catch (IOException | URISyntaxException e) {
                e.printStackTrace();
            }

        }
    }
}
