package com.edumate.boot.domain.admin.model.service.impl;

import com.edumate.boot.domain.admin.model.service.AdminService;
import com.edumate.boot.domain.admin.model.mapper.AdminMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper adminMapper;

}
