package com.edumate.boot.domain.notice.model.service.impl;

import com.edumate.boot.domain.notice.model.service.NoticeService;
import com.edumate.boot.domain.notice.model.mapper.NoticeMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeMapper noticeMapper;

}
