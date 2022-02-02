package com.willdev.openvpn.fromanother.util.util;

public class Events {


    public static class InfoUpdate {

        private String id, type, status_layout, status_type, view, total_like, already_like;
        private int position;

        public InfoUpdate(String id, String type, String status_layout, String status_type, String view, String total_like, String already_like, int position) {
            this.id = id;
            this.type = type;
            this.status_layout = status_layout;
            this.status_type = status_type;
            this.view = view;
            this.total_like = total_like;
            this.already_like = already_like;
            this.position = position;
        }

        public String getId() {
            return id;
        }

        public String getType() {
            return type;
        }

        public String getStatus_layout() {
            return status_layout;
        }

        public String getStatus_type() {
            return status_type;
        }

        public String getView() {
            return view;
        }

        public String getTotal_like() {
            return total_like;
        }

        public String getAlready_like() {
            return already_like;
        }

        public int getPosition() {
            return position;
        }
    }


    public static class DownloadUpdate {

        private String id, status_type, download_count;

        public DownloadUpdate(String id, String status_type, String download_count) {
            this.id = id;
            this.status_type = status_type;
            this.download_count = download_count;
        }

        public String getId() {
            return id;
        }

        public String getStatus_type() {
            return status_type;
        }

        public String getDownload_count() {
            return download_count;
        }
    }


    public static class StopPlay {
        private String play;

        public StopPlay(String play) {
            this.play = play;
        }

        public String getPlay() {
            return play;
        }
    }


    public static class FavouriteNotify {
        private String id, status_layout, is_favourite, status_type;

        public FavouriteNotify(String id, String status_layout, String is_favourite, String status_type) {
            this.id = id;
            this.status_layout = status_layout;
            this.is_favourite = is_favourite;
            this.status_type = status_type;
        }

        public String getId() {
            return id;
        }

        public String getStatus_layout() {
            return status_layout;
        }

        public String getIs_favourite() {
            return is_favourite;
        }

        public String getStatus_type() {
            return status_type;
        }
    }


    public static class RewardNotify {
        private String reward;

        public RewardNotify(String reward) {
            this.reward = reward;
        }

        public String getReward() {
            return reward;
        }
    }


    public static class AddComment {
        private String comment_id, user_id, user_name, user_image, post_id, status_type, comment_text, comment_date, total_comment;

        public AddComment(String comment_id, String user_id, String user_name, String user_image, String post_id, String status_type, String comment_text, String comment_date, String total_comment) {
            this.comment_id = comment_id;
            this.user_id = user_id;
            this.user_name = user_name;
            this.user_image = user_image;
            this.post_id = post_id;
            this.status_type = status_type;
            this.comment_text = comment_text;
            this.comment_date = comment_date;
            this.total_comment = total_comment;
        }

        public String getComment_id() {
            return comment_id;
        }

        public void setComment_id(String comment_id) {
            this.comment_id = comment_id;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getUser_image() {
            return user_image;
        }

        public void setUser_image(String user_image) {
            this.user_image = user_image;
        }

        public String getPost_id() {
            return post_id;
        }

        public void setPost_id(String post_id) {
            this.post_id = post_id;
        }

        public String getStatus_type() {
            return status_type;
        }

        public void setStatus_type(String status_type) {
            this.status_type = status_type;
        }

        public String getComment_text() {
            return comment_text;
        }

        public void setComment_text(String comment_text) {
            this.comment_text = comment_text;
        }

        public String getComment_date() {
            return comment_date;
        }

        public void setComment_date(String comment_date) {
            this.comment_date = comment_date;
        }

        public String getTotal_comment() {
            return total_comment;
        }

        public void setTotal_comment(String total_comment) {
            this.total_comment = total_comment;
        }
    }


    public static class DeleteComment {

        private String total_comment, post_id, comment_id, type;

        public DeleteComment(String total_comment, String post_id, String comment_id, String type) {
            this.total_comment = total_comment;
            this.post_id = post_id;
            this.comment_id = comment_id;
            this.type = type;
        }

        public String getTotal_comment() {
            return total_comment;
        }

        public String getPost_id() {
            return post_id;
        }

        public String getComment_id() {
            return comment_id;
        }

        public String getType() {
            return type;
        }
    }


    public static class Login {
        private String login;

        public Login(String login) {
            this.login = login;
        }

        public String getLogin() {
            return login;
        }
    }


    public static class ImageStatusNotify {
        private String imageNotify;

        public ImageStatusNotify(String imageNotify) {
            this.imageNotify = imageNotify;
        }

        public String getImageNotify() {
            return imageNotify;
        }

    }


    public static class VideoStatusNotify {
        private String videoNotify;

        public VideoStatusNotify(String videoNotify) {
            this.videoNotify = videoNotify;
        }

        public String getVideoNotify() {
            return videoNotify;
        }

    }


    public static class FullScreenNotify {
        private boolean fullscreen;

        public FullScreenNotify(boolean fullscreen) {
            this.fullscreen = fullscreen;
        }

        public boolean isFullscreen() {
            return fullscreen;
        }
    }


    public static class Select {

        private String string;

        public Select(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }


    public static class UploadFinish {

        private String upload;

        public UploadFinish(String upload) {
            this.upload = upload;
        }

        public String getUpload() {
            return upload;
        }
    }


    public static class Language {

        private String type;

        public Language(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }
    }

}
