INSERT INTO `multi-functional-chat`.`role` (`id`, `name`) VALUES ('1', 'ADMIN');
INSERT INTO `multi-functional-chat`.`role` (`id`, `name`) VALUES ('2', 'MODERATOR');
INSERT INTO `multi-functional-chat`.`role` (`id`, `name`) VALUES ('3', 'USER');

INSERT INTO `multi-functional-chat`.`user` (`id`, `block`, `username`, `password`, `role_id`) VALUES ('1', b'0', 'admin', '$2a$10$s6LlURcK8La80s1.SeyP2eldwtLjTFJdruUysDG5zCJNw0tcFyUl.', '2');
INSERT INTO `multi-functional-chat`.`user` (`id`, `block`, `username`, `password`, `role_id`) VALUES ('2', b'0', 'yBot', '$2a$10$gwu1j7pG0UluYaqRGBCD5ukpIt7.Qpn3qcaszNYExWyvScPKZh5iO', '1');

INSERT INTO `multi-functional-chat`.`chat` (`id`, `is_private`, `name`, `user_id_creator`) VALUES ('1', b'0', 'yBot', '2');
