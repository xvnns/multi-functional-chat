package com.example.multifunctionalchat.controller;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.example.multifunctionalchat.cli.room.*;
import com.example.multifunctionalchat.cli.splitter.InputCommandSplitter;
import com.example.multifunctionalchat.cli.user.UserBanCommand;
import com.example.multifunctionalchat.cli.user.UserModeratorCommand;
import com.example.multifunctionalchat.cli.user.UserRenameCommand;
import com.example.multifunctionalchat.domain.Chat;
import com.example.multifunctionalchat.domain.Message;
import com.example.multifunctionalchat.domain.RoleName;
import com.example.multifunctionalchat.domain.User;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.ChatNotFoundException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import com.example.multifunctionalchat.service.BotService;
import com.example.multifunctionalchat.service.ChatService;
import com.example.multifunctionalchat.service.MessageService;
import com.example.multifunctionalchat.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class BotController {
    @Autowired
    private UserService userService;
    @Autowired
    private ChatService chatService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private BotService botService;

    private JCommander jCommander;

    @GetMapping("/chat-bot")
    public String chatBot(Model model) {
        try {
            Chat chat = chatService.getChatByName("yBot");
            botService.reloadUsers();
            model.addAttribute("messages", chat.getMessages());
            model.addAttribute("message", new Message());
            model.addAttribute("chat_id", chat.getId());
        } catch (ChatNotFoundException e) {
            model.addAttribute("error", e.getMessage());
        }
        return "chatBotRoom";
    }

    @PostMapping("/chat-bot")
    public String readStr(Message message, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        InputCommandSplitter inputCommandSplitter = new InputCommandSplitter();
        String[] command = inputCommandSplitter.split(message.getContent()).toArray(new String[0]);
        switch (command[0]) {
            case ("//user"):
                try {
                    userCommand(command, user);
                } catch (AddingToTheDatabaseException|ParameterException e) {
                    e.printStackTrace();
                }
                break;

            case ("//room"):
                try {
                    roomCommand(command, user);
                } catch (AddingToTheDatabaseException | ChatNotFoundException | DeleteFromDatabaseException |
                        ParameterException e) {
                    e.printStackTrace();
                }
                break;

            case ("//yBot"):
                break;
        }

        try {
            Chat chat = chatService.getChatByName("yBot");
            messageService.sendMessage(chat.getId(), (User) userService.loadUserByUsername("yBot"),
                    "Команда успешно выпонена");
            messageService.sendMessage(chat.getId(), user, message.getContent());
        } catch (ChatNotFoundException e) {
            e.printStackTrace();
        }
        return "redirect:/chat-bot";
    }

    public void userCommand(String[] command, User registeredUser) throws AddingToTheDatabaseException, ParameterException {
        String login;
        String newLogin;
        switch (command[1]) {
            case ("rename"):
                UserRenameCommand userRenameCommand = new UserRenameCommand();
                jCommander = JCommander.newBuilder()
                        .addCommand(userRenameCommand)
                        .build();
                jCommander.parse(command);
                login = userRenameCommand.getUserLogin();
                newLogin = userRenameCommand.getNewUserLogin();
                userService.updateLogin(login, newLogin, registeredUser);
                break;
            case ("ban"):
                UserBanCommand userBanCommand = new UserBanCommand();
                jCommander = JCommander.newBuilder()
                        .addCommand(userBanCommand)
                        .build();
                jCommander.parse(command);
                login = userBanCommand.getLogin();
                userService.blockUser(login);
                break;
            case ("moderator"):
                UserModeratorCommand userModeratorCommand = new UserModeratorCommand();
                jCommander = JCommander.newBuilder()
                        .addCommand(userModeratorCommand)
                        .build();
                jCommander.parse(command);
                login = userModeratorCommand.getUserLogin();
                if (userModeratorCommand.isModerator()) {
                    userService.updateUserRole(login, RoleName.MODERATOR);
                } else {
                    userService.updateUserRole(login, RoleName.USER);
                }
                break;
        }
    }

    public void roomCommand(String[] command, User user) throws AddingToTheDatabaseException, ChatNotFoundException, DeleteFromDatabaseException {
        String room;
        String userLogin;
        switch (command[1]) {
            case ("create"):
                RoomCreateCommand roomCreateCommand = new RoomCreateCommand();
                jCommander = JCommander.newBuilder()
                        .addCommand(roomCreateCommand)
                        .build();
                jCommander.parse(command);
                room = roomCreateCommand.getRoomName();
                chatService.createPrivateRoom(room, user);
                break;
            case ("remove"):
                RoomRemoveCommand roomRemoveCommand = new RoomRemoveCommand();
                jCommander = JCommander.newBuilder()
                        .addCommand(roomRemoveCommand)
                        .build();
                jCommander.parse(command);
                room = roomRemoveCommand.getRoomName();
                chatService.removeRoom(room);
                break;
            case ("rename"):
                RoomRenameCommand roomRenameCommand = new RoomRenameCommand();
                jCommander = JCommander.newBuilder()
                        .addCommand(roomRenameCommand)
                        .build();
                jCommander.parse(command);
                room = roomRenameCommand.getRoomName();
                String newName = roomRenameCommand.getNewRoomName();
                chatService.renameRoom(room, newName);
                break;
            case ("connect"):
                RoomConnectCommand roomConnectCommand = new RoomConnectCommand();
                jCommander = JCommander.newBuilder()
                        .addCommand(roomConnectCommand)
                        .build();
                jCommander.parse(command);
                room = roomConnectCommand.getRoomName();
                userLogin = roomConnectCommand.getUserLogin();
                chatService.addUserByLogin(room, userLogin);
                break;
            case ("disconnect"):
                RoomDisconnectCommand roomDisconnectCommand = new RoomDisconnectCommand();
                jCommander = JCommander.newBuilder()
                        .addCommand(roomDisconnectCommand)
                        .build();
                jCommander.parse(command);
                room = roomDisconnectCommand.getRoomName();
                if (roomDisconnectCommand.getUserLogin()!= null) {
                    userLogin = roomDisconnectCommand.getUserLogin();
                    chatService.deleteUserByLogin(room, userLogin);
                }
                else {
                    chatService.deleteUserByLogin(room, user.getUsername());
                }
                break;
        }
    }
}
