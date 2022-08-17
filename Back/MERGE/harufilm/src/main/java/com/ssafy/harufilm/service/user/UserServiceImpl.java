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

        // [1] 새 설명이 있을경우
        if (modifyRequestDto.getUserdesc() != null)
            user.setUserdesc(modifyRequestDto.getUserdesc());

        // [2] 새 유저 이미지 파일이 들어 있을 경우
        if (modifyRequestDto.getUserimg() != null) {
            MultipartFile newimg = modifyRequestDto.getUserimg();
            String imgpath = "/var/opt/upload/profile/";

            // 1. profile_userimg.png 가 아닌 다른 파일을 가리킬 경우 해당 파일 삭제
            String curimg = user.getUserimg();

            if (!curimg.equals("baseimg.png")) {
                File curfile = new File(imgpath + curimg);
                try {
                    curfile.delete();
                } catch (Exception e) {
                }
            }

            // 2. DB업데이트 반드시 이미지 이름으로 하되 pid를 붙여서 저장 ex) pid_이미지이름.png
            String updateimg = Integer.toString(user.getUserpid()) + "_" + newimg.getOriginalFilename();
            user.setUserimg(updateimg);

            // 3. 해당 파일 서버에 저장
            File updatefile = new File(imgpath + updateimg);
            newimg.transferTo(updatefile);
        }

        // [3] 새 유저 이름이 있을경우
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

        // 1. 프로필 파일 삭제
        String imgpath = "/var/opt/upload/profile/";
        String curimg = user.getUserimg();
        if (!curimg.equals("baseimg.png")) {
            File curfile = new File(imgpath + curimg);
            try {
                curfile.delete();
            } catch (Exception e) {
            }
        }
        // 2. 게시글 파일 삭제
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

        // 3. 구독기록 삭제
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
            System.out.println("구독자 없음");
            // TODO: handle exception
        }
        try {
            // 4. 좋아요 기록 삭제
            List<Likey> likeyfromlist = likeyRepository.findByLikeyfrom(user.getUserpid()); // 내가 좋아요한 기록 삭제

            for (int i = 0; i < likeyfromlist.size(); ++i) {
                int likeyidx = likeyfromlist.get(i).getLikeyidx();
                likeyRepository.deleteById(likeyidx);
            }

        } catch (

        Exception e) {
            System.out.println("좋아요 없음");
            // TODO: handle exception
        }

        try {
            List<Article> articlelist = articleRepository.findAllByUserpid(user.getUserpid());

            for (int i = 0; i < articlelist.size(); ++i) {
                int articleidx = articlelist.get(i).getArticleidx();

                // 내 게시글에 대한 좋아요 기록 삭제
                List<Likey> likeytolist = likeyRepository.findByLikeyto(articleidx);
                for (int j = 0; j < likeytolist.size(); ++j) {
                    int likeyidx = likeytolist.get(j).getLikeyidx();
                    likeyRepository.deleteById(likeyidx);
                }

                // 5. 해시태그 삭제
                // [articleidx] -> Hashtag -> [hashidx] -> Hash 접근
                List<Hashtag> hashtaglist = hashtagRepository.findByArticleidx(articleidx);
                for (int htag = 0; htag < hashtaglist.size(); ++htag) {
                    int hashtagidx = hashtaglist.get(htag).getHashtagidx();
                    int hashidx = hashtaglist.get(htag).getHashidx();

                    hashRepository.deleteById(hashidx);
                    hashtagRepository.deleteById(hashtagidx);
                }

                // 6. 게시글 삭제
                articleRepository.deleteById(articleidx);

            }
        } catch (

        Exception e) {
            System.out.println("게시글 없음");
            // TODO: handle exception
        }

        // 7. 유저 삭제
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
