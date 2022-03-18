package com.patriot.bot.listeners;

import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KickListener implements MessageCreateListener {
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if(event.getMessageContent().startsWith("патриот кикни")){
            List<User> users = event.getMessage().getMentionedUsers();
            Pattern p = Pattern.compile("патриот кик([\\s\\d*]*)");
            Matcher m = p.matcher(event.getMessageContent());
            if(event.getMessageAuthor().canKickUsersFromServer()){
                if (!users.isEmpty()){
                    EmbedBuilder eb = new EmbedBuilder().setTitle("Кик").setDescription("Были кикнуты:").setColor(Color.green);
                    for (User user :
                            users) {
                        eb.addField("Пользователь", user.getMentionTag());
                        event.getServer().get().kickUser(user);
                    }
                    try{
                        event.getMessage().addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));
                        Message msg = event.getChannel().sendMessage(eb).get();
                        try{
                            TimeUnit.SECONDS.sleep(5);
                            msg.edit(new EmbedBuilder().setTitle("Пользователь кикнут").setColor(Color.green));
                        } catch (InterruptedException e){
                            e.printStackTrace();
                        }
                    } catch (InterruptedException | ExecutionException e){
                        e.printStackTrace();
                    }
                } else if (m.matches()) {
                    String[] ids = event.getMessageContent().substring(m.start(1), m.end(1)).trim().split("\\s");
                    if(!ids[0].trim().isEmpty()){
                        EmbedBuilder eb = new EmbedBuilder().setTitle("Кик").setDescription("Были кикнуты:").setColor(Color.green);
                        boolean cm=false;
                        for (String id :
                                ids) {
                            if (event.getServer().get().getMemberById(id).isPresent()){
                                eb.addField("Пользователь", "<@" + id + ">");
                                try {
                                    event.getServer().get().kickUser(event.getApi().getUserById(id).get());
                                    cm=true;
                                } catch (InterruptedException | ExecutionException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                eb.setTitle("Ошибка").setDescription("Пользователь не найден").setColor(Color.red);
                            }
                        }
                        try {
                            if (cm) event.getMessage().addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));
                            else event.getMessage().addReaction(EmojiParser.parseToUnicode(":x:"));
                            Message msg = event.getChannel().sendMessage(eb).get();
                            try {
                                TimeUnit.SECONDS.sleep(5);
                                if (cm) msg.edit(new EmbedBuilder().setColor(Color.green).setTitle("Пользователь кикнут"));
                                else msg.delete();
                            } catch (InterruptedException e){
                                e.printStackTrace();
                            }
                        }catch (InterruptedException|ExecutionException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
