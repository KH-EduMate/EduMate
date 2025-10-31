package com.edumate.boot.app.reference.controller;

import com.edumate.boot.domain.reference.model.service.ReferenceService;
import com.edumate.boot.domain.reference.model.vo.Reference;
import com.edumate.boot.domain.member.model.vo.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpSession;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/reference")
@RequiredArgsConstructor
public class ReferenceController {

    private final ReferenceService referenceService;

    // ==============================
    // 📄 전체 목록
    // ==============================
    @GetMapping("/list")
    public String showReferenceList(
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            HttpSession session,
            Model model) {
        try {
            String memberId = (String) session.getAttribute("loginId");
            String adminYn = (String) session.getAttribute("adminYn");
            String teacherYn = (String) session.getAttribute("teacherYn");

            boolean canWrite = false;
            if (memberId != null) {
                canWrite = "Y".equals(adminYn) ||
                           "Y".equals(teacherYn);
            }
            model.addAttribute("canWrite", canWrite);

            int totalCount = referenceService.getTotalCount();
            int boardCountPerPage = 5;
            int maxPage = (int) Math.ceil((double) totalCount / boardCountPerPage);

            int offset = (currentPage - 1) * boardCountPerPage;
            java.util.Map<String, Object> paramMap = new java.util.HashMap<>();
            paramMap.put("offset", offset);
            paramMap.put("limit", boardCountPerPage);

            List<Reference> rList = referenceService.selectList(paramMap);

            int naviCountPerPage = 5;
            int startNavi = ((currentPage - 1) / naviCountPerPage) * naviCountPerPage + 1;
            int endNavi = (startNavi - 1) + naviCountPerPage;
            if (endNavi > maxPage) endNavi = maxPage;

            // ✅ 이전/다음 버튼 표시 여부 계산
            boolean showPrev = currentPage > naviCountPerPage;
            boolean showNext = maxPage > naviCountPerPage && endNavi < maxPage;

            model.addAttribute("currentPage", currentPage);
            model.addAttribute("maxPage", maxPage);
            model.addAttribute("startNavi", startNavi);
            model.addAttribute("endNavi", endNavi);
            model.addAttribute("rList", rList);
            model.addAttribute("showPrev", showPrev);
            model.addAttribute("showNext", showNext);

            return "reference/list";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "common/error";
        }
    }

    // ==============================
    // 📄 상세보기
    // ==============================
    @GetMapping("/detail")
    public String showReferenceDetailView(
            @RequestParam("archiveNo") int archiveNo,
            HttpSession session,
            Model model) {
        try {
            referenceService.updateViewCount(archiveNo);
            Reference reference = referenceService.selectOneByNo(archiveNo);

            String memberId = (String) session.getAttribute("loginId");
            String adminYn = (String) session.getAttribute("adminYn");

            boolean canModify = false;
            if (memberId != null) {
                canModify = reference.getMemberId().equals(memberId) ||
                            "Y".equals(adminYn);
            }

            model.addAttribute("reference", reference);
            model.addAttribute("canModify", canModify);
            return "reference/detail";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "common/error";
        }
    }

    // ==============================
    // 📄 등록 페이지
    // ==============================
    @GetMapping("/insert")
    public String showInsertView(HttpSession session, Model model) {
        String memberId = (String) session.getAttribute("loginId");
        String adminYn = (String) session.getAttribute("adminYn");
        String teacherYn = (String) session.getAttribute("teacherYn");

        if (memberId == null ||
            (!"Y".equals(adminYn) && !"Y".equals(teacherYn))) {
            return "redirect:/reference/list";
        }

        return "reference/insert";
    }

    // ==============================
    // 📄 등록 처리
    // ==============================
    @PostMapping("/insert")
    public String insertReference(
            @ModelAttribute Reference reference,
            @RequestParam(value = "uploadFile", required = false) MultipartFile uploadFile,
            HttpSession session,
            Model model) {
        try {
            String memberId = (String) session.getAttribute("loginId");
            String adminYn = (String) session.getAttribute("adminYn");
            String teacherYn = (String) session.getAttribute("teacherYn");


            if (memberId == null ||
                (!"Y".equals(adminYn) && !"Y".equals(teacherYn))) {
                return "redirect:/reference/list";
            }

            reference.setMemberId(memberId);

            if (uploadFile != null && !uploadFile.getOriginalFilename().isBlank()) {
                String originalName = uploadFile.getOriginalFilename();
                String renamedName = UUID.randomUUID().toString() + "_" + originalName;

                String folderPath = session.getServletContext().getRealPath("/resources/references");
                uploadFile.transferTo(new File(folderPath + File.separator + renamedName));

                reference.setAttachmentName(originalName);
                reference.setAttachmentRename(renamedName);
                reference.setAttachmentPath("/resources/references/" + renamedName);
            }

            referenceService.insertReference(reference);
            return "redirect:/reference/list";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "common/error";
        }
    }

    // ==============================
    // 📄 수정 페이지
    // ==============================
    @GetMapping("/modify")
    public String showModifyView(
            @RequestParam int archiveNo,
            HttpSession session,
            Model model) {
        try {
            String memberId = (String) session.getAttribute("loginId");
            String adminYn = (String) session.getAttribute("adminYn");
            Reference reference = referenceService.selectOneByNo(archiveNo);

            if (memberId == null ||
                (!reference.getMemberId().equals(memberId) &&
                !"Y".equals(adminYn))) {
                return "redirect:/reference/list";
            }

            model.addAttribute("reference", reference);
            return "reference/modify";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "common/error";
        }
    }

    // ==============================
    // 📄 수정 처리
    // ==============================
    @PostMapping("/modify")
    public String updateReference(
            @ModelAttribute Reference reference,
            @RequestParam(value = "reloadFile", required = false) MultipartFile reloadFile,
            HttpSession session,
            Model model) {
        try {
            String memberId = (String) session.getAttribute("loginId");
            String adminYn = (String) session.getAttribute("adminYn");

            Reference originalRef = referenceService.selectOneByNo(reference.getArchiveNo());

            if (memberId == null ||
                (!originalRef.getMemberId().equals(memberId) &&
                !"Y".equals(adminYn))) {
                return "redirect:/reference/list";
            }

            if (reloadFile != null && !reloadFile.getOriginalFilename().isBlank()) {
                String originalName = reloadFile.getOriginalFilename();
                String renamedName = UUID.randomUUID().toString() + "_" + originalName;

                String folderPath = session.getServletContext().getRealPath("/resources/archiveFiles");
                reloadFile.transferTo(new File(folderPath + File.separator + renamedName));

                reference.setAttachmentName(originalName);
                reference.setAttachmentRename(renamedName);
                reference.setAttachmentPath("/resources/archiveFiles/" + renamedName);
            }

            referenceService.updateReference(reference);
            return "redirect:/reference/detail?archiveNo=" + reference.getArchiveNo();
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "common/error";
        }
    }

    // ==============================
    // 📄 삭제
    // ==============================
    @GetMapping("/delete")
    public String deleteReference(
            @RequestParam int archiveNo,
            HttpSession session,
            Model model) {
        try {
            String memberId = (String) session.getAttribute("loginId");
            String adminYn = (String) session.getAttribute("adminYn");

            Reference reference = referenceService.selectOneByNo(archiveNo);

            if (memberId == null ||
                (!reference.getMemberId().equals(memberId) &&
                !"Y".equals(adminYn))) {
                return "redirect:/reference/list";
            }

            referenceService.deleteReference(archiveNo);
            return "redirect:/reference/list";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "common/error";
        }
    }

    // ==============================
    // 📄 카테고리별 목록
    // ==============================
    @GetMapping("/category")
    public String showCategoryList(
            @RequestParam("category") String category,
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            HttpSession session,
            Model model) {
        try {
            String memberId = (String) session.getAttribute("loginId");
            String adminYn = (String) session.getAttribute("adminYn");
            String teacherYn = (String) session.getAttribute("teacherYn");

            boolean canWrite = false;
            if (memberId != null) {
                canWrite = "Y".equals(adminYn) ||
                           "Y".equals(teacherYn);
            }
            model.addAttribute("canWrite", canWrite);

            int boardLimit = 5;
            int offset = (currentPage - 1) * boardLimit;

            java.util.Map<String, Object> paramMap = new java.util.HashMap<>();
            paramMap.put("category", category);
            paramMap.put("offset", offset);
            paramMap.put("limit", boardLimit);

            List<Reference> categoryList = referenceService.selectListByCategory(paramMap);
            int totalCount = referenceService.getTotalCountByCategory(category);

            int maxPage = (int) Math.ceil((double) totalCount / boardLimit);
            int naviLimit = 5;
            int startNavi = ((currentPage - 1) / naviLimit) * naviLimit + 1;
            int endNavi = (startNavi - 1) + naviLimit;
            if (endNavi > maxPage) endNavi = maxPage;

            boolean showPrev = currentPage > naviLimit;
            boolean showNext = maxPage > naviLimit && endNavi < maxPage;

            model.addAttribute("category", category);
            model.addAttribute("currentPage", currentPage);
            model.addAttribute("maxPage", maxPage);
            model.addAttribute("startNavi", startNavi);
            model.addAttribute("endNavi", endNavi);
            model.addAttribute("rList", categoryList);
            model.addAttribute("showPrev", showPrev);
            model.addAttribute("showNext", showNext);

            return "reference/list";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "common/error";
        }
    }

    // ==============================
    // 📄 검색 결과 목록
    // ==============================
    @GetMapping("/search")
    public String showSearchList(
            @RequestParam(value = "searchCondition", defaultValue = "all") String searchCondition,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "page", defaultValue = "1") int currentPage,
            HttpSession session,
            Model model) {
        try {
            String memberId = (String) session.getAttribute("loginId");
            String adminYn = (String) session.getAttribute("adminYn");
            String teacherYn = (String) session.getAttribute("teacherYn");

            boolean canWrite = false;
            if (memberId != null) {
                canWrite = "Y".equals(adminYn) ||
                           "Y".equals(teacherYn);
            }
            model.addAttribute("canWrite", canWrite);

            if (searchKeyword == null || searchKeyword.trim().isEmpty()) {
                return "redirect:/reference/list";
            }

            int boardLimit = 5;
            int offset = (currentPage - 1) * boardLimit;

            java.util.Map<String, Object> searchMap = new java.util.HashMap<>();
            searchMap.put("searchCondition", searchCondition);
            searchMap.put("searchKeyword", searchKeyword.trim());
            searchMap.put("offset", offset);
            searchMap.put("limit", boardLimit);

            List<Reference> searchList = referenceService.selectSearchList(searchMap);
            int totalCount = referenceService.getTotalCountBySearch(searchMap);

            int maxPage = (int) Math.ceil((double) totalCount / boardLimit);
            int naviLimit = 5;
            int startNavi = ((currentPage - 1) / naviLimit) * naviLimit + 1;
            int endNavi = (startNavi - 1) + naviLimit;
            if (endNavi > maxPage) endNavi = maxPage;

            boolean showPrev = currentPage > naviLimit;
            boolean showNext = maxPage > naviLimit && endNavi < maxPage;

            model.addAttribute("currentPage", currentPage);
            model.addAttribute("maxPage", maxPage);
            model.addAttribute("startNavi", startNavi);
            model.addAttribute("endNavi", endNavi);
            model.addAttribute("searchCondition", searchCondition);
            model.addAttribute("searchKeyword", searchKeyword);
            model.addAttribute("searchList", searchList);
            model.addAttribute("showPrev", showPrev);
            model.addAttribute("showNext", showNext);

            return "reference/list";
        } catch (Exception e) {
            model.addAttribute("errorMsg", e.getMessage());
            return "common/error";
        }
    }
}
