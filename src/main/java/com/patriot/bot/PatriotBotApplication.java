package com.patriot.bot;

import com.patriot.bot.listeners.KickListener;
import com.patriot.bot.listeners.MessageListener;
import com.patriot.bot.listeners.UnbanListener;
import org.javacord.api.DiscordApi;
import org.javacord.api.DiscordApiBuilder;
import org.javacord.api.entity.activity.ActivityType;
import org.javacord.api.entity.message.MessageBuilder;
import org.javacord.api.entity.message.embed.EmbedBuilder;
import org.javacord.api.entity.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootApplication
public class PatriotBotApplication {
	@Autowired
	public Environment env;
	public static void main(String[] args) {
		SpringApplication.run(PatriotBotApplication.class, args);
	}
	@Bean
	@ConfigurationProperties(value="discord-api")
	public DiscordApi discordApi(){
		String token = env.getProperty("TOKEN");
		DiscordApi api = new DiscordApiBuilder().setToken(token)
				.setAllNonPrivilegedIntents()
				.login()
				.join();
		api.updateActivity(ActivityType.PLAYING, "Zа Победу");
		api.addListener(new MessageListener());
		api.addListener(new UnbanListener());
		api.addListener(new KickListener());
		return api;
	}


}
