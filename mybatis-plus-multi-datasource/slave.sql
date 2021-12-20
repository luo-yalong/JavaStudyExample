/*
 Navicat Premium Data Transfer

 Source Server         : 本地
 Source Server Type    : MySQL
 Source Server Version : 50736
 Source Host           : localhost:3306
 Source Schema         : slave

 Target Server Type    : MySQL
 Target Server Version : 50736
 File Encoding         : 65001

 Date: 20/12/2021 16:56:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `name` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '姓名',
  `sex` tinyint(1) NULL DEFAULT 1 COMMENT '1：男  2：女',
  `age` int(11) NULL DEFAULT 0 COMMENT '年龄',
  `created` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `modify` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  `version` int(11) NULL DEFAULT 0 COMMENT '版本',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'slave-张三', 1, 19, '2021-12-20 14:23:47', '2021-12-20 15:58:02', 0);
INSERT INTO `user` VALUES (2, 'slave-李四', 1, 19, '2021-12-20 15:01:14', '2021-12-20 15:58:02', 0);
INSERT INTO `user` VALUES (3, 'slave-李明', 1, 19, '2021-12-20 15:02:08', '2021-12-20 15:58:02', 0);

SET FOREIGN_KEY_CHECKS = 1;
