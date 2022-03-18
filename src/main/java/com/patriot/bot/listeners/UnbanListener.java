package com.patriot.bot.listeners;

import com.patriot.bot.PatriotBotApplication;
import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.server.Ban;
import org.javacord.api.entity.server.Server;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnbanListener implements MessageCreateListener {
    private PatriotBotApplication checkUser = new PatriotBotApplication();

    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageContent().startsWith("патриот разбань")) {
            if (event.getMessageAuthor().isServerAdmin()) {
                List<User> users = event.getMessage().getMentionedUsers();
                Pattern pattern = Pattern.compile("патриот разбань([\\s\\d*]*)");
                Matcher matcher = pattern.matcher(event.getMessageContent());
                if (!users.isEmpty()) {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Разбан")
                            .setDescription("Были разбанены:")
                            .setColor(Color.green);
                    for (User user :
                            users) {
                        eb.addField("Пользователь", "<@" + user.getIdAsString() + ">", true);
                        event.getServer().get().unbanUser(user);
                    }
                    Message msg;
                    try {
                        event.getMessage().addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));
                        msg = event.getChannel().sendMessage(eb).get();
                        TimeUnit.SECONDS.sleep(5);
                        msg.edit(new EmbedBuilder().setColor(Color.green).setTitle("Пользователь разбанен"));
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                } else if (matcher.matches()) {
                    String[] ids = event.getMessageContent().substring(matcher.start(1), matcher.end(1)).trim().split("\\s");
                    if (!ids[0].trim().isEmpty()) {
                        EmbedBuilder eb = new EmbedBuilder();
                        boolean cm = false;
                        eb.setTitle("Разбан")
                                .setDescription("Были разбанены:")
                                .setColor(Color.green);

                        for (String id :
                                ids) {
                            try {
                                Collection<Ban>banCollection = event.getServer().get().getBans().get();
                                List<String>banIds = new ArrayList<String>();
                                for (Ban ban :
                                        banCollection) {
                                    banIds.add(ban.getUser().getIdAsString());
                                }
                                if (banIds.contains(id)) {
                                    eb.addField("Пользователь", "<@" + id + ">", true);
                                    event.getServer().get().unbanUser(id);
                                    cm=true;
                                } else {
                                    eb.setColor(Color.red).setTitle("Ошибка").setDescription("Пользователь не найден");
                                }
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        Message msg;
                        try {
                            if(cm){event.getMessage().addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));}
                            else event.getMessage().addReaction(EmojiParser.parseToUnicode(":x:"));
                            msg = event.getChannel().sendMessage(eb).get();
                            try {
                                TimeUnit.SECONDS.sleep(5);
                                if(cm)msg.edit(new EmbedBuilder().setColor(Color.green).setTitle("Пользователь разбанен"));
                                else msg.delete();
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }

                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            event.getMessage().addReaction(EmojiParser.parseToUnicode(":x:"));
                            Message msg = event.getChannel().sendMessage(new EmbedBuilder()
                                    .setTitle("Ошибка")
                                    .setDescription("Неверное id/имя пользователя, или оно не введено.")
                                    .addField("Примеры правильного синтаксиса:", "патриот разбань <@951386910989877269> \n патриот разбань 951386910989877269")
                                    .setColor(Color.red)
                            ).get();
                            try{
                                TimeUnit.SECONDS.sleep(15);
                                msg.delete();
                            }catch (InterruptedException e){
                                e.printStackTrace();
                            }

                        } catch (InterruptedException | ExecutionException e){
                            e.printStackTrace();
                        }
                    }
//						System.out.println(matcher.matches());
                }
            } else if (!event.getMessageAuthor().isServerAdmin()) {
                try {
                    event.getMessage().addReaction(EmojiParser.parseToUnicode(":x:"));
                    Message msg = event.getChannel().sendMessage(new EmbedBuilder()
                            .setTitle("Ошибка")
                            .setDescription("Ты не Путин")
                            .setColor(Color.red)
                    ).get();
                    try {
                        TimeUnit.SECONDS.sleep(10);
                        msg.delete();
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }

                } catch (InterruptedException | ExecutionException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
