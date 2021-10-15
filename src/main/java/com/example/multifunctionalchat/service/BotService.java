package com.example.multifunctionalchat.service;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.example.multifunctionalchat.api.YouTubeApi;
import com.example.multifunctionalchat.cli.bot.YBotChannelInfo;
import com.example.multifunctionalchat.cli.bot.YBotFindCommand;
import com.example.multifunctionalchat.cli.bot.YBotHelpCommand;
import com.example.multifunctionalchat.cli.room.*;
import com.example.multifunctionalchat.cli.splitter.InputCommandSplitter;
import com.example.multifunctionalchat.cli.user.UserBanCommand;
import com.example.multifunctionalchat.cli.user.UserModeratorCommand;
import com.example.multifunctionalchat.cli.user.UserRenameCommand;
import com.example.multifunctionalchat.domain.*;
import com.example.multifunctionalchat.exception.AddingToTheDatabaseException;
import com.example.multifunctionalchat.exception.ChatNotFoundException;
import com.example.multifunctionalchat.exception.DeleteFromDatabaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class BotService {

    @Autowired
    UserService userService;
    @Autowired
    ChatService chatService;
    @Autowired
    MessageService messageService;

    private JCommander jCommander;

    /*public void reloadUsers() throws ChatNotFoundException {
        Chat chat = chatService.getChatByName("yBot");
        List<User> chatBotUsers = chat.getUsers();
        for (User user : userService.getAll()) {
            if (!chatBotUsers.contains(user)) {
                chatBotUsers.add(user);
            }
        }*/
        // messageService.sendMessage(chat.getId(), (User) userService.loadUserByUsername("yBot"), loadHelpMessage());
   // }

    public void getCommand(String comm, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        InputCommandSplitter inputCommandSplitter = new InputCommandSplitter();
        String[] command = inputCommandSplitter.split(comm).toArray(new String[0]);

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setAuthor("yBot");
        chatMessage.setRoom("yBot");

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
                switch (command[1]) {
                    case ("find"):
                        find(command);
                        //отправить рез-т юзеру
                        break;
                    case ("help") :
                        help(command);
                        //отправить рез-т юзеру
                        break;
                    case ("channelInfo") :
                        channelInfo(command);
                        //отправить рез-т юзеру
                        break;
                    case ("videoCommentRandom") :
                        //botService.help(command);
                        //отправить рез-т юзеру
                        break;
                }
                break;
        }
    }

    public String find(String[] command) {
        StringBuilder ms = new StringBuilder();
        YBotFindCommand yBotFindCommand = new YBotFindCommand();
        jCommander = JCommander.newBuilder().addCommand(yBotFindCommand).build();
        jCommander.parse(command);
        YouTubeApi youTubeApi = new YouTubeApi();

        try {
            ms.append(youTubeApi.getUrl(yBotFindCommand.getNames().get(0), yBotFindCommand
                    .getNames().get(1)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (yBotFindCommand.isLikesNumber()) {
            try {
                ms.append(youTubeApi.likeCount(yBotFindCommand.getNames().get(0), yBotFindCommand
                        .getNames().get(1)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (yBotFindCommand.isViewsNumber()) {
            try {
                ms.append(youTubeApi.viewCount(yBotFindCommand.getNames().get(0), yBotFindCommand
                        .getNames().get(1)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return ms.toString();
    }

    public void help(String[] command) {
        YBotHelpCommand yBotHelpCommand = new YBotHelpCommand();
        jCommander = JCommander.newBuilder().addCommand(yBotHelpCommand).build();
        jCommander.parse(command);
    }
    public void channelInfo(String[] command) {
        jCommander = JCommander.newBuilder().addCommand(new YBotChannelInfo()).build();
        jCommander.parse(command);
    }

    public void userCommand(String[] command, User registeredUser) throws AddingToTheDatabaseException, ParameterException {
        String login;
        String newLogin;
        switch (command[1]) {
            case ("rename"):
                UserRenameCommand userRenameCommand = new UserRenameCommand();
                jCommander = JCommander.newBuilder().addCommand(userRenameCommand).build();
                jCommander.parse(command);
                login = userRenameCommand.getUserLogin();
                newLogin = userRenameCommand.getNewUserLogin();
                userService.updateLogin(login, newLogin, registeredUser);
                break;
            case ("ban"):
                UserBanCommand userBanCommand = new UserBanCommand();
                jCommander = JCommander.newBuilder().addCommand(userBanCommand).build();
                jCommander.parse(command);
                login = userBanCommand.getLogin();
                userService.blockUser(login);
                break;
            case ("moderator"):
                UserModeratorCommand userModeratorCommand = new UserModeratorCommand();
                jCommander = JCommander.newBuilder().addCommand(userModeratorCommand).build();
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
                jCommander = JCommander.newBuilder().addCommand(roomCreateCommand).build();
                jCommander.parse(command);
                room = roomCreateCommand.getRoomName();
                chatService.createPrivateRoom(room, user);
                break;
            case ("remove"):
                RoomRemoveCommand roomRemoveCommand = new RoomRemoveCommand();
                jCommander = JCommander.newBuilder().addCommand(roomRemoveCommand).build();
                jCommander.parse(command);
                room = roomRemoveCommand.getRoomName();
                chatService.removeRoom(room);
                break;
            case ("rename"):
                RoomRenameCommand roomRenameCommand = new RoomRenameCommand();
                jCommander = JCommander.newBuilder().addCommand(roomRenameCommand).build();
                jCommander.parse(command);
                room = roomRenameCommand.getRoomName();
                String newName = roomRenameCommand.getNewRoomName();
                chatService.renameRoom(room, newName);
                break;
            case ("connect"):
                RoomConnectCommand roomConnectCommand = new RoomConnectCommand();
                jCommander = JCommander.newBuilder().addCommand(roomConnectCommand).build();
                jCommander.parse(command);
                room = roomConnectCommand.getRoomName();
                userLogin = roomConnectCommand.getUserLogin();
                chatService.addUserByLogin(room, userLogin);
                break;
            case ("disconnect"):
                RoomDisconnectCommand roomDisconnectCommand = new RoomDisconnectCommand();
                jCommander = JCommander.newBuilder().addCommand(roomDisconnectCommand).build();
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