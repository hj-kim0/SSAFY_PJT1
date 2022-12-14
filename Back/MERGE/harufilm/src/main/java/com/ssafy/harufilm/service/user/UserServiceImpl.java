package com.ssafy.harufilm.service.user;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.ssafy.harufilm.dto.account.SignupRequestDto;
import com.ssafy.harufilm.dto.account.SmallProfileResponseDto;
import com.ssafy.harufilm.dto.profile.ModifyRequestDto;
import com.ssafy.harufilm.entity.Article;
import com.ssafy.harufilm.entity.Hashtag;
import com.ssafy.harufilm.entity.Likey;
import com.ssafy.harufilm.entity.Subscribe;
import com.ssafy.harufilm.entity.User;
import com.ssafy.harufilm.repository.article.ArticleRepository;
import com.ssafy.harufilm.repository.hash.HashRepository;
import com.ssafy.harufilm.repository.hashtag.HashtagRepository;
import com.ssafy.harufilm.repository.likey.LikeyRepository;
import com.ssafy.harufilm.repository.subscribe.SubscribeRepository;
import com.ssafy.harufilm.repository.user.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubscribeRepository subscribeRepository;

    @Autowired
    private LikeyRepository likeyRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    @Autowired
    private HashRepository hashRepository;

    @Override
    public User getuserbyId(String userid) {
        return userRepository.findByUserid(userid).orElse(null);
    }

    @Override
    public User getuserbyPid(int userpid) {
        return userRepository.findByUserpid(userpid);
    }

    @Transactional
    @Override
    public User userNewSave(SignupRequestDto signupRequestDto) {

        String userimg = "baseimg.png";

        User user = User.builder()
                .userid(signupRequestDto.getUserid())
                .userpassword(signupRequestDto.getUserpassword())
                .username(signupRequestDto.getUsername())
                .userimg(userimg)
                .userdesc("")
                .userpwq(signupRequestDto.getUserpwq())
                .userpwa(signupRequestDto.getUserpwa())
                .roles("USER")
                .build();

        return userRepository.save(user);
    }

    @Override
    public void modifyprofile(ModifyRequestDto modifyRequestDto) throws IllegalStateException, IOException {

        User user = userRepository.findByUserpid(modifyRequestDto.getUserpid());

        // [1] ??? ????????? ????????????
        if (modifyRequestDto.getUserdesc() != null)
            user.setUserdesc(modifyRequestDto.getUserdesc());

        // [2] ??? ?????? ????????? ????????? ?????? ?????? ??????
        if (modifyRequestDto.getUserimg() != null) {
            MultipartFile newimg = modifyRequestDto.getUserimg();
            String imgpath = "/var/opt/upload/profile/";

            // 1. profile_userimg.png ??? ?????? ?????? ????????? ????????? ?????? ?????? ?????? ??????
            String curimg = user.getUserimg();

            if (!curimg.equals("baseimg.png")) {
                File curfile = new File(imgpath + curimg);
                try {
                    curfile.delete();
                } catch (Exception e) {
                }
            }

            // 2. DB???????????? ????????? ????????? ???????????? ?????? pid??? ????????? ?????? ex) pid_???????????????.png
            String updateimg = Integer.toString(user.getUserpid()) + "_" + newimg.getOriginalFilename();
            user.setUserimg(updateimg);

            // 3. ?????? ?????? ????????? ??????
            File updatefile = new File(imgpath + updateimg);
            newimg.transferTo(updatefile);
        }

        // [3] ??? ?????? ????????? ????????????
        if (modifyRequestDto.getUsername() != null)
            user.setUsername(modifyRequestDto.getUsername());

        userRepository.save(user);

    }

    @Override
    public List<SmallProfileResponseDto> getuserlistbykeyword(String keyword) {
        List<User> userlist = userRepository.findByUsernameOrUseridContaining(keyword, keyword);
        List<SmallProfileResponseDto> usersplist = new ArrayList<>();

        for (int i = 0; i < userlist.size(); i++) {
            SmallProfileResponseDto spDto = new SmallProfileResponseDto();
            spDto.setUserid(userlist.get(i).getUserid());
            spDto.setUsername(userlist.get(i).getUsername());
            spDto.setUserimg(userlist.get(i).getUserimg());
            spDto.setUserpid(userlist.get(i).getUserpid());
            usersplist.add(spDto);
        }
        return usersplist;
    }

    @Override
    public void modifypassword(String userid, String enuserpassword) {
        User user = userRepository.findByUserid(userid).orElse(null);
        if (user != null) {
            user.setUserpassword(enuserpassword);
            userRepository.save(user);
        }
    }

    @Override
    public void signdown(User user) {
        // TODO Auto-generated method stub

        // 1. ????????? ?????? ??????
        String imgpath = "/var/opt/upload/profile/";
        String curimg = user.getUserimg();
        if (!curimg.equals("baseimg.png")) {
            File curfile = new File(imgpath + curimg);
            try {
                curfile.delete();
            } catch (Exception e) {
            }
        }
        // 2. ????????? ?????? ??????
        String path = "/var/opt/upload/article/" + user.getUserpid();
        File folder = new File(path);

        if (folder.exists()) {
            try {
                FileUtils.cleanDirectory(folder);
                if (folder.isDirectory())
                    folder.delete();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }

        // 3. ???????????? ??????
        try {

            List<Subscribe> subfromlist = subscribeRepository.findBySubfrom(user.getUserpid());

            List<Subscribe> subtolist = subscribeRepository.findBySubto(user.getUserpid());

            for (int i = 0; i < subfromlist.size(); ++i) {
                int subidx = subfromlist.get(i).getSubidx();
                subscribeRepository.deleteById(subidx);
            }
            for (int i = 0; i < subtolist.size(); ++i) {
                int subidx = subtolist.get(i).getSubidx();
                subscribeRepository.deleteById(subidx);
            }
        } catch (Exception e) {
            System.out.println("????????? ??????");
            // TODO: handle exception
        }
        try {
            // 4. ????????? ?????? ??????
            List<Likey> likeyfromlist = likeyRepository.findByLikeyfrom(user.getUserpid()); // ?????? ???????????? ?????? ??????

            for (int i = 0; i < likeyfromlist.size(); ++i) {
                int likeyidx = likeyfromlist.get(i).getLikeyidx();
                likeyRepository.deleteById(likeyidx);
            }

        } catch (

        Exception e) {
            System.out.println("????????? ??????");
            // TODO: handle exception
        }

        try {
            List<Article> articlelist = articleRepository.findAllByUserpid(user.getUserpid());

            for (int i = 0; i < articlelist.size(); ++i) {
                int articleidx = articlelist.get(i).getArticleidx();

                // ??? ???????????? ?????? ????????? ?????? ??????
                List<Likey> likeytolist = likeyRepository.findByLikeyto(articleidx);
                for (int j = 0; j < likeytolist.size(); ++j) {
                    int likeyidx = likeytolist.get(j).getLikeyidx();
                    likeyRepository.deleteById(likeyidx);
                }

                // 5. ???????????? ??????
                // [articleidx] -> Hashtag -> [hashidx] -> Hash ??????
                List<Hashtag> hashtaglist = hashtagRepository.findByArticleidx(articleidx);
                for (int htag = 0; htag < hashtaglist.size(); ++htag) {
                    int hashtagidx = hashtaglist.get(htag).getHashtagidx();
                    int hashidx = hashtaglist.get(htag).getHashidx();

                    hashRepository.deleteById(hashidx);
                    hashtagRepository.deleteById(hashtagidx);
                }

                // 6. ????????? ??????
                articleRepository.deleteById(articleidx);

            }
        } catch (

        Exception e) {
            System.out.println("????????? ??????");
            // TODO: handle exception
        }

        // 7. ?????? ??????
        userRepository.deleteById(user.getUserpid());

    }

    @Override
    public void setuserfcmtoken(int userpid, String userfcmtoken) {

        User user = userRepository.findByUserpid(userpid);
        user.setUserfcmtoken(userfcmtoken);
        userRepository.save(user);
        // TODO Auto-generated method stub

    }

    @Override
    public String getuserfcmtoken(int userpid) {
        // TODO Auto-generated method stub
        return userRepository.findByUserpid(userpid).getUserfcmtoken();
    }

    @Override
    public void deletefcmtoken(int userpid) {

        User user = userRepository.findByUserpid(userpid);
        user.setUserfcmtoken(null);
        userRepository.save(user);
        // TODO Auto-generated method stub

    }
}
