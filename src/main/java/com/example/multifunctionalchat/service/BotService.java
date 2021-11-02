package com.example.multifunctionalchat.service;

import com.beust.jcommander.JCommander;
import com.example.multifunctionalchat.api.YouTubeApi;
import com.example.multifunctionalchat.cli.bot.*;
import com.example.multifunctionalchat.cli.room.*;
import com.example.multifunctionalchat.cli.splitter.InputCommandSplitter;
import com.example.multifunctionalchat.cli.user.*;
import com.example.multifunctionalchat.domain.*;
import com.example.multifunctionalchat.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.Date;

@Service
public class BotService {

    @Autowired
    public UserService userService;

    @Autowired
    public ChatService chatService;

    @Autowired
    public MessageService messageService;

    public JCommander jCommander;

    public void getCommand(String comm, User user) throws AddingToTheDatabaseException, ChatNotFoundException, DeleteFromDatabaseException {
        Message botMessage = new Message();
        botMessage.setChat(chatService.getChatByName("yBot"));
        botMessage.setUser((User) userService.loadUserByUsername("yBot"));
        botMessage.setDate(new Date());

        InputCommandSplitter inputCommandSplitter = new InputCommandSplitter();
        String[] command = inputCommandSplitter.split(comm).toArray(new String[0]);
        switch (command[0]) {
            case ("//user"):
                botMessage.setContent(userCommand(command, user));
                break;
            case ("//room"):
                botMessage.setContent(roomCommand(command, user));
                break;
            case ("//yBot"):
                switch (command[1]) {
                    case ("find"):
                        botMessage.setContent(find(command));
                        break;
                    case ("help") :
                        //help(command);
                        break;
                    case ("channelInfo") :
                        botMessage.setContent(channelInfo(command));
                        break;
                    default:
                        botMessage.setContent("Не удалось распознать команду");
                }
                break;
            default:
                botMessage.setContent("Не удалось распознать команду");
        }
        messageService.save(botMessage);
    }

    public String find(String[] command) {
        StringBuilder ms = new StringBuilder();
        YBotFindCommand yBotFindCommand = new YBotFindCommand();
        jCommander = JCommander.newBuilder().addCommand(yBotFindCommand).build();
        jCommander.parse(command);
        YouTubeApi youTubeApi = new YouTubeApi();
        try {
            ms.append(youTubeApi.getUrl(yBotFindCommand.getNames().get(0), yBotFindCommand.getNames().get(1)));
        } catch (IOException e) {
            ms.append(e.getMessage());
        }
        if (yBotFindCommand.isLikesNumber()) {
            try {
                ms.append(youTubeApi.likeCount(yBotFindCommand.getNames().get(0), yBotFindCommand.getNames().get(1)));
            } catch (IOException e) {
                ms.append(e.getMessage());
            }
        }
        if (yBotFindCommand.isViewsNumber()) {
            try {
                ms.append(youTubeApi.viewCount(yBotFindCommand.getNames().get(0), yBotFindCommand.getNames().get(1)));
            } catch (IOException e) {
                ms.append(e.getMessage());
            }
        }
        return ms.toString();
    }

    public void help(String[] command) {
        YBotHelpCommand yBotHelpCommand = new YBotHelpCommand();
        jCommander = JCommander.newBuilder().addCommand(yBotHelpCommand).build();
        jCommander.parse(command);
    }

    public String channelInfo(String[] command) {
        StringBuilder ms = new StringBuilder();
        YBotChannelInfo yBotChannelInfo = new YBotChannelInfo();
        jCommander = JCommander.newBuilder().addCommand(yBotChannelInfo).build();
        jCommander.parse(command);
        YouTubeApi youTubeApi = new YouTubeApi();
        try {
            ms.append(youTubeApi.channelInfo(yBotChannelInfo.getName()));
        } catch (IOException e) {
            ms.append(e.getMessage());
        }
        return ms.toString();
    }

    public String userCommand(String[] command, User registeredUser) {
        String str;
        switch (command[1]) {
            case ("rename"):
                UserRenameCommand userRenameCommand = new UserRenameCommand();
                jCommander = JCommander.newBuilder().addCommand(userRenameCommand).build();
                jCommander.parse(command);
                String login = userRenameCommand.getUserLogin();
                String newLogin = userRenameCommand.getNewUserLogin();
                userService.renameUser((User) userService.loadUserByUsername(login), newLogin, registeredUser);
                str = "Пользователь переименован";
                break;
            case ("ban"):
                UserBanCommand userBanCommand = new UserBanCommand();
                jCommander = JCommander.newBuilder().addCommand(userBanCommand).build();
                jCommander.parse(command);
                login = userBanCommand.getLogin();
                userService.blockUser((User) userService.loadUserByUsername(login));
                str = "Пользователь заблокирован";
                break;
            case ("moderator"):
                UserModeratorCommand userModeratorCommand = new UserModeratorCommand();
                jCommander = JCommander.newBuilder().addCommand(userModeratorCommand).build();
                jCommander.parse(command);
                login = userModeratorCommand.getUserLogin();
                User user = (User) userService.loadUserByUsername(login);
                try {
                    if (userModeratorCommand.isModerator()) {
                        userService.makeModerator(user);
                        str = "Пользователь назначен модератором";
                    } else {
                       userService.makeUser(user);
                       str = "Пользователь больше не является модератором";
                    }
                } catch (EditRoleException e) {
                    str = e.getMessage();
                }
                break;
            default:
                str = "Не удалось распознать команду";
                break;
        }
        return str;
    }

    public String roomCommand(String[] command, User registeredUser) {
        String room;
        String userLogin;
        String str;
        switch (command[1]) {
            case ("create"):
                RoomCreateCommand roomCreateCommand = new RoomCreateCommand();
                jCommander = JCommander.newBuilder().addCommand(roomCreateCommand).build();
                jCommander.parse(command);
                room = roomCreateCommand.getRoomName();
                try {
                    if (roomCreateCommand.isPrivate()) {
                        chatService.createPrivateRoom(room, registeredUser);
                    }
                    else {
                        chatService.createRoom(room, registeredUser);
                    }
                    str = "Комната создана";
                } catch (AddingToTheDatabaseException e) {
                    str = e.getMessage();
                }
                break;
            case ("remove"):
                RoomRemoveCommand roomRemoveCommand = new RoomRemoveCommand();
                jCommander = JCommander.newBuilder().addCommand(roomRemoveCommand).build();
                jCommander.parse(command);
                room = roomRemoveCommand.getRoomName();
                try {
                    chatService.removeRoom(chatService.getChatByName(room), registeredUser);
                    str = "Пользователь был удален из чата";
                } catch (ChatNotFoundException e) {
                    str = e.getMessage();
                }
                break;
            case ("rename"):
                RoomRenameCommand roomRenameCommand = new RoomRenameCommand();
                jCommander = JCommander.newBuilder().addCommand(roomRenameCommand).build();
                jCommander.parse(command);
                room = roomRenameCommand.getRoomName();
                String newName = roomRenameCommand.getNewRoomName();
                try {
                    chatService.renameRoom(chatService.getChatByName(room), newName, registeredUser);
                    str = "Комната была переименована";
                } catch (ChatNotFoundException e) {
                    str = e.getMessage();
                }
                break;
            case ("connect"):
                RoomConnectCommand roomConnectCommand = new RoomConnectCommand();
                jCommander = JCommander.newBuilder().addCommand(roomConnectCommand).build();
                jCommander.parse(command);
                room = roomConnectCommand.getRoomName();
                userLogin = roomConnectCommand.getUserLogin();
                try {
                    chatService.addUser(chatService.getChatByName(room), (User) userService.loadUserByUsername(userLogin),
                            registeredUser);
                    str = "Пользователь был добавлен в чат";
                } catch (AddingToTheDatabaseException | ChatNotFoundException e) {
                    str = e.getMessage();
                }
                break;
            case ("disconnect"):
                RoomDisconnectCommand roomDisconnectCommand = new RoomDisconnectCommand();
                jCommander = JCommander.newBuilder().addCommand(roomDisconnectCommand).build();
                jCommander.parse(command);
                room = roomDisconnectCommand.getRoomName();
                try {
                    if (roomDisconnectCommand.getUserLogin()!= null) {
                        userLogin = roomDisconnectCommand.getUserLogin();
                        chatService.deleteUser(chatService.getChatByName(room),
                                (User) userService.loadUserByUsername(userLogin), registeredUser);
                    }
                    else {
                        chatService.deleteUser(chatService.getChatByName(room), registeredUser, registeredUser);
                    }
                    str = "Пользователь был удален из чата";
                } catch (DeleteFromDatabaseException | ChatNotFoundException e) {
                    str = e.getMessage();
                }
                break;
            default:
                str = "Не удалось распознать команду";
                break;
        }
        return str;
    }
}