<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ page import="java.util.*, java.text.*" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8" />
<meta name="viewport" content="width=device-width, initial-scale=1.0" />
<title>${event.eventTitle} - 이벤트 상세</title>
<link rel="stylesheet" href="/resources/css/event/event_detail.css" />
<link rel="stylesheet" href="/resources/css/common/header.css" />
<link rel="stylesheet" href="/resources/css/common/footer.css" />
</head>
<body>
    <jsp:include page="../common/header.jsp" />
    
<main>
<link rel="stylesheet" href="/resources/css/common/main_banner.css">
<section class="main-banner">
            <div class="banner-text">
                이벤트 정보
            </div>
            <div class="object">
                <img src="/resources/images/event/icon/event_icon.png">
            </div>
</section>
    
    <section class="event-detail">
        <%
            SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
            Date now = new Date();
            Date startDate = null;
            Date endDate = null;
            try {
                startDate = sdf.parse(String.valueOf(request.getAttribute("event")).replaceAll(".*eventStart=(.*?),.*", "$1"));
                endDate = sdf.parse(String.valueOf(request.getAttribute("event")).replaceAll(".*eventEnd=(.*?),.*", "$1"));
            } catch (Exception e) { }

            boolean isOngoing = false;
            long remainDays = 0;
            if (startDate != null && endDate != null) {
                isOngoing = now.after(startDate) && now.before(endDate);
                remainDays = (endDate.getTime() - now.getTime()) / (1000 * 60 * 60 * 24);
            }
        %>

        <div class="event-header">
            <div class="event-path">이벤트 &gt; ${event.eventTitle}</div>
            <div class="event-meta">
                <span>
                    <fmt:formatDate value="${event.eventStart}" pattern="yyyy-MM-dd" /> ~ 
                    <fmt:formatDate value="${event.eventEnd}" pattern="yyyy-MM-dd" />
                </span>
                <span class="event-status <%= isOngoing ? "on" : "end" %>">
                    <%= isOngoing ? "진행중" : "종료" %>
                    <% if (isOngoing && remainDays <= 3 && remainDays >= 0) { %>
                        <span style="font-weight:500; color:yellow;">(D-<%= remainDays %>)</span>
                    <% } %>
                </span>
            </div>
        </div>
        <div class="event-buttons">
            <div class="event-buttons-left">
                <c:if test="${sessionScope.loginMember.adminYN eq 'Y'}">
                    <a href="/event/update?eventId=${event.eventId}" style="text-decoration:none">
                        <!-- <button class="edit-btn">수정</button> -->
                    </a> 
                    <a href="javascript:void(0)" onclick="checkDelete();" style="text-decoration:none">
                        <button class="delete-btn">삭제</button>
                    </a>
                </c:if>
            </div>

            <div class="event-buttons-right">
                <c:if test="${not empty prevEventId}">
                    <button class="prev-btn" onclick="location.href='/event/detail?eventId=${prevEventId}'">이전</button>
                </c:if>
                <c:if test="${not empty nextEventId}">
                    <button class="next-btn" onclick="location.href='/event/detail?eventId=${nextEventId}'">다음</button>
                </c:if>
                <button class="list-btn" onclick="location.href='/event/list'">목록</button>
            </div>
        </div>
        <div class="event-content">

            <c:if test="${not empty contents}">
                <div class="event-content-list">
                    <c:forEach var="content" items="${contents}">
                        <div class="event-content-item">
                            <c:set var="contentStr" value="${content.toString()}" />
                            <c:set var="startPath" value="${fn:substringAfter(contentStr, 'eContentPath=\\'')}" />
                            <c:set var="imagePath" value="${fn:substringBefore(startPath, '\\'')}" />
                            <img src="${imagePath}" alt="Event Content Image" />
                        </div>
                    </c:forEach>
                </div>
            </c:if>

            <c:if test="${empty contents}">
                <div class="no-content-message">
                    <p>추가 이미지가 없습니다.</p>
                </div>
            </c:if>
        </div>

        <div class="event-buttons">
            <div class="event-buttons-left">
                <c:if test="${sessionScope.loginMember.adminYN eq 'Y'}">
                    <a href="/event/update?eventId=${event.eventId}" style="text-decoration:none">
                        <!-- <button class="edit-btn">수정</button> -->
                    </a>
                    <a href="javascript:void(0)" onclick="checkDelete();" style="text-decoration:none">
                        <button class="delete-btn">삭제</button>
                    </a>
                </c:if>
            </div>

            <div class="event-buttons-right">
                <c:if test="${not empty prevEventId}">
                    <button class="prev-btn" onclick="location.href='/event/detail?eventId=${prevEventId}'">이전</button>
                </c:if>
                <c:if test="${not empty nextEventId}">
                    <button class="next-btn" onclick="location.href='/event/detail?eventId=${nextEventId}'">다음</button>
                </c:if>
                <button class="list-btn" onclick="location.href='/event/list'">목록</button>
            </div>
        </div>
    </section>
</main>

<jsp:include page="../common/footer.jsp" />

<script>
    function checkDelete() {
        if (confirm('정말로 삭제하시겠습니까?')) {
            location.href = '/event/delete?eventId=${event.eventId}';
        }
    }
</script>
</body>
</html>
