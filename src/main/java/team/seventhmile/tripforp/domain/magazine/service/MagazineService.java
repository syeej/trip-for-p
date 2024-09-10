package team.seventhmile.tripforp.domain.magazine.service;

import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import team.seventhmile.tripforp.domain.file.entity.MagazineFile;
import team.seventhmile.tripforp.domain.file.service.MagazineFileService;
import team.seventhmile.tripforp.domain.magazine.dto.MagazineDto;
import team.seventhmile.tripforp.domain.magazine.entity.Magazine;
import team.seventhmile.tripforp.domain.magazine.repository.MagazineRepository;
import team.seventhmile.tripforp.domain.user.entity.Role;
import team.seventhmile.tripforp.domain.user.entity.User;
import team.seventhmile.tripforp.domain.user.repository.UserRepository;
import team.seventhmile.tripforp.global.exception.ResourceNotFoundException;
import team.seventhmile.tripforp.global.exception.UnauthorizedAccessException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MagazineService {

    private final MagazineRepository magazineRepository;
    private final UserRepository userRepository;
    private final MagazineFileService magazineFileService;


    // 매거진 글 작성하기
    @Transactional
    public MagazineDto createMagazinePost(MagazineDto magazineDto, String userEmail,
        List<MultipartFile> files) throws IOException {

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException(User.class));

        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException(Magazine.class);
        }

        Magazine magazine = magazineDto.convertToEntity(user);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                MagazineFile magazineFile = magazineFileService.saveFile(file);
                magazine.addFile(magazineFile);
            }
        }
        magazineRepository.save(magazine);

        return MagazineDto.convertToDto(magazine);
    }

    // 매거진 글 수정하기
    @Transactional
    public MagazineDto updateMagazinePost(Long id, MagazineDto magazineDto, String userEmail,
        List<MultipartFile> files) throws IOException {

        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException(User.class));

        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException(Magazine.class);
        }

        Magazine magazine = magazineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Magazine.class));

        // 업데이트
        magazine.update(magazineDto.getTitle(), magazineDto.getContent());
        if (magazine.getFiles() != null) {
            for (MagazineFile file : magazine.getFiles()) {
                magazineFileService.deleteFile(file.getFileName());
            }
        }
		magazine.clearFile();

		if (files != null && !files.isEmpty()) {
			for (MultipartFile file : files) {
				MagazineFile magazineFile = magazineFileService.saveFile(file);
				magazine.addFile(magazineFile);
			}
		}

        return MagazineDto.convertToDto(magazine);
    }

    // 매거진 글 삭제
    @Transactional
    public void deleteMagazinePost(Long id, String userEmail) {

        Magazine magazine = magazineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Magazine.class));

        // 현재 로그인된 사용자 가져오기
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException(User.class));

        // 권한 확인
        if (user.getRole() != Role.ADMIN) {
            throw new UnauthorizedAccessException(Magazine.class);
        }

        if (magazine.getFiles() != null) {
            for (MagazineFile file : magazine.getFiles()) {
                magazineFileService.deleteFile(file.getFileName());
            }
        }

        magazineRepository.delete(magazine);
    }

    // 매거진 글 목록 조회
    @Transactional(readOnly = true)
    public Page<MagazineDto> getAllMagazineList(Pageable pageable) {

        return magazineRepository.findAllByOrderByCreatedAtDesc(pageable)
            .map(MagazineDto::convertToDto);
    }

    // 매거진 글 상세 조회
    @Transactional
    public MagazineDto getMagazineDetail(Long id) {
        Magazine magazine = magazineRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(Magazine.class));

        // 조회 수 증가
        magazine.incrementViews();

        return MagazineDto.convertToDto(magazine);
    }

    // 리뷰 게시글 검색(제목, 내용) 조회
    @Transactional(readOnly = true)
    public Page<MagazineDto> getMagazineSearch(String keyword, Pageable pageable) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Magazine> magazines = magazineRepository.getMagazineKeywordContaining(
            keyword.trim(), pageable);
        return magazines.map(MagazineDto::convertToDto);

    }
}
