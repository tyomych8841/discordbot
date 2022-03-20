package com.patriot.bot.listeners;
import com.vdurmont.emoji.EmojiParser;
import org.javacord.api.entity.message.Message;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.permission.Role;
import org.javacord.api.entity.user.User;
import org.javacord.api.event.message.MessageCreateEvent;
import org.javacord.api.listener.message.MessageCreateListener;
import java.awt.*;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageListener implements MessageCreateListener {
    private final Locale locale = new Locale("ru-RU");
    private ConfigListener cl = new ConfigListener();
    @Override
    public void onMessageCreate(MessageCreateEvent event) {
        if (event.getMessageContent().toLowerCase(locale).contains("слава украине")||
            event.getMessageContent().toLowerCase(locale).contains("славаукраине")||
                event.getMessageContent().toLowerCase(locale).contains("украине слава")||
                event.getMessageContent().toLowerCase(locale).contains("украинеслава")||
                event.getMessageContent().toLowerCase(locale).contains("москаль")||
                event.getMessageContent().toLowerCase(locale).contains("кацап")||
                event.getMessageContent().toLowerCase(locale).contains("ватник")||
                event.getMessageContent().toLowerCase(locale).contains("путин хуйло")||
                event.getMessageContent().toLowerCase(locale).contains("москали")||
                event.getMessageContent().toLowerCase(locale).contains("кацапы")||
                event.getMessageContent().toLowerCase(locale).contains("ватники")
        ) {
                event.getMessage().reply( new EmbedBuilder()
                        .setTitle("Обнаружен хохол! Групповая эвакуация из беседы")
                        .setDescription("Имя хохла - "+event.getMessageAuthor().asUser().get().getMentionTag())
                        .setThumbnail("https://thumbs.dreamstime.com/b/%D0%BA%D1%80%D0%B8%D0%B7%D0%B8%D1%81-%D1%83%D0%BA%D1%80%D0%B0%D0%B8%D0%BD%D1%81%D0%BA%D0%BE%D0%B9-%D1%8D%D0%BA%D0%BE%D0%BD%D0%BE%D0%BC%D0%B8%D0%BA%D0%B8-%D0%BA%D0%BE%D0%BD%D1%86%D0%B5%D0%BF%D1%86%D0%B8%D1%8F-74258390.jpg")
                        .setColor(Color.yellow));
                if(cl.getXoxolRole(event)!=null) {
                    List<Role> roles = event.getMessageAuthor().asUser().get().getRoles(event.getServer().get());
                    for (Role role :
                            roles) {
                        event.getMessageAuthor().asUser().get().removeRole(role);
                    }
                    event.getMessageAuthor().asUser().get().addRole(cl.getXoxolRole(event));
                } else {
                    event.getServer().get().getOwner().get().getPrivateChannel().get().sendMessage("Не установлена роль хохла.\nДля установки, напишите:\n" +
                            "**патриот роль хохла @роль**\n**патриот роль хохла 123456789123456**");
                }
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    event.getMessage().delete();
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
        }
        if (event.getMessageContent().startsWith("патриот забань")) {
            if (event.getMessageAuthor().isServerAdmin()) {
                List<User> users = event.getMessage().getMentionedUsers();
                Pattern pattern = Pattern.compile("патриот забань([\\s\\d*]*)");
                Matcher matcher = pattern.matcher(event.getMessageContent());
                if (!users.isEmpty()) {
                    EmbedBuilder eb = new EmbedBuilder();
                    eb.setTitle("Бан")
                            .setDescription("Были забанены:")
                            .setColor(Color.green);
                    for (User user :
                            users) {
                        eb.addField("Пользователь", "<@" + user.getIdAsString() + ">", true);
                        event.getServer().get().banUser(user);
                    }
                    Message msg;
                    try {
                        event.getMessage().addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));
                        msg = event.getChannel().sendMessage(eb).get();
                        try {
                            TimeUnit.SECONDS.sleep(5);
                            msg.edit(new EmbedBuilder().setColor(Color.green).setTitle("Пользователь забанен"));
                        }catch (InterruptedException e){
                            e.printStackTrace();
                        }

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                } else if (matcher.matches()) {
                    String[] ids = event.getMessageContent().substring(matcher.start(1), matcher.end(1)).trim().split("\\s");
                    if (!ids[0].trim().isEmpty()) {
                        EmbedBuilder eb = new EmbedBuilder();
                        Boolean cm = false;
                        eb.setTitle("Бан")
                                .setDescription("Были забанены:")
                                .setColor(Color.green);
                        for (String id :
                                ids) {
                            if (event.getServer().get().getMemberById(id).isPresent()) {
                                eb.addField("Пользователь", "<@" + id + ">", true);
                                event.getServer().get().banUser(id);
                                cm = true;
                            } else {
                                            eb.setColor(Color.red)
                                                    .setTitle("Ошибка").setDescription("Пользователь не найден");
                            }
                        }
                        Message msg;
                        try {
                            if(cm){
                                event.getMessage().addReaction(EmojiParser.parseToUnicode(":white_check_mark:"));
                            } else event.getMessage().addReaction(EmojiParser.parseToUnicode(":x:"));
                            msg = event.getChannel().sendMessage(eb).get();
                            try {
                                TimeUnit.SECONDS.sleep(5);
                                if (cm) msg.edit(new EmbedBuilder().setColor(Color.green).setTitle("Пользователь забанен"));
                                else msg.delete();
                            } catch (InterruptedException e){
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
                                    .setDescription("Неверный id/имя пользователя, или оно не введено.")
                                    .addField("Примеры правильного синтаксиса:", "патриот забань <@951386910989877269> \n патриот забань 951386910989877269")
                                    .setColor(Color.red)
                            ).get();
                            try {
                                TimeUnit.SECONDS.sleep(15);
                                msg.delete();
                            } catch (InterruptedException e){
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
                        TimeUnit.SECONDS.sleep(20);msg.delete();
                    } catch (InterruptedException e){
                        e.printStackTrace();
                    }


                }catch (InterruptedException | ExecutionException e){
                    e.printStackTrace();
                }

            }
        }
    }}


