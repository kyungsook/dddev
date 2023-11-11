package com.d103.dddev.api.request.controller;

import com.d103.dddev.api.common.ResponseDto;
import com.d103.dddev.api.request.collection.Comment;
import com.d103.dddev.api.request.collection.Request;
import com.d103.dddev.api.request.repository.dto.requestDto.*;
import com.d103.dddev.api.request.repository.dto.responseDto.RequestStepResponseDto;
import com.d103.dddev.api.request.repository.dto.responseDto.RequestTreeResponseDto;
import com.d103.dddev.api.request.service.RequestServiceImpl;
import com.d103.dddev.common.exception.document.DocumentNotFoundException;
import com.d103.dddev.common.exception.user.UserNotFoundException;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.management.InvalidAttributeValueException;
import java.util.List;

@RestController
@RequestMapping("/ground/{groundId}/request")
@RequiredArgsConstructor
@Api(tags = {"요청 문서 API"})
public class RequestController {
    private final RequestServiceImpl requestService;

    @PostMapping("/create")
    @ApiOperation(value="요청 문서 생성")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<Request>> insertRequest(@ApiParam(value = "그라운드 아이디") @PathVariable("groundId") int groundId,
                                           @ApiParam(value = "step1문서 생성할 때 parentId 필요없음\n" +
                                                   "미분류로 생성할 때 parentId 미분류 문서 id\n" +
                                                   "title -> not required 없으면 빈 문자열 \"\"로 생성")@RequestBody RequestInsertOneDto requestInsertOneDto,
                                           @RequestHeader String Authorization,
                                           @AuthenticationPrincipal UserDetails userDetails){
        ResponseDto<Request> responseDto;

        try{
            Request request = requestService.insertRequest(groundId, requestInsertOneDto, userDetails);
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.OK.value())
                    .message("요청 문서가 생성되었습니다.")
                    .data(request)
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(DocumentNotFoundException e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
        }catch(InvalidAttributeValueException e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.UNPROCESSABLE_ENTITY);
        }catch(Exception e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/titles")
    @ApiOperation(value="제목들로 step1 요청 문서들 생성")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<List<Request>>> insertRequestsWithTitles(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                                               @RequestBody RequestInsertManyDto requestInsertManyDto,
                                                                               @RequestHeader String Authorization,
                                                                               @AuthenticationPrincipal UserDetails userDetails){
        ResponseDto<List<Request>> responseDto;

        try{
            List<Request> requestList = requestService.insertRequestsWithTitles(groundId, requestInsertManyDto, userDetails);
            responseDto = ResponseDto.<List<Request>>builder()
                    .code(HttpStatus.OK.value())
                    .message("요청 문서들이 생성되었습니다.")
                    .data(requestList)
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(Exception e){
            responseDto = ResponseDto.<List<Request>>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{requestId}")
    @ApiOperation(value="요청 문서 상세 조회")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<Request>> getRequest(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                           @PathVariable("requestId") String RequestId,
                                                           @RequestHeader String Authorization){
        ResponseDto<Request> responseDto;

        try{
            Request Request = requestService.getRequest(groundId, RequestId);
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.OK.value())
                    .message("요청 문서를 불러왔습니다.")
                    .data(Request)
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(DocumentNotFoundException e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
        }catch(InvalidAttributeValueException e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.UNPROCESSABLE_ENTITY);
        }catch(Exception e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/total")
    @ApiOperation(value="문서트리 구조로 불러오기")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<List<RequestTreeResponseDto>>> getTreeRequests(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                                                      @RequestHeader String Authorization){
        ResponseDto<List<RequestTreeResponseDto>> responseDto;

        try{
            List<RequestTreeResponseDto> requests = requestService.getTreeRequests(groundId);
            responseDto = ResponseDto.<List<RequestTreeResponseDto>>builder()
                    .code(HttpStatus.OK.value())
                    .message("문서 트리를 불러왔습니다.")
                    .data(requests)
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(Exception e){
            responseDto = ResponseDto.<List<RequestTreeResponseDto>>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/list")
    @ApiOperation(value="step1 문서들 불러오기")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<List<RequestStepResponseDto>>> getStep1Requests(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                                                      @RequestHeader String Authorization){
        ResponseDto<List<RequestStepResponseDto>> responseDto;

        try{
            List<RequestStepResponseDto> requests = requestService.getStep1Requests(groundId);
            responseDto = ResponseDto.<List<RequestStepResponseDto>>builder()
                    .code(HttpStatus.OK.value())
                    .message("문서 트리를 불러왔습니다.")
                    .data(requests)
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(Exception e){
            responseDto = ResponseDto.<List<RequestStepResponseDto>>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/step2")
    @ApiOperation(value="step2 문서들 불러오기")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<List<Request>>> getStep2Requests(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                                       @ApiParam(value = "인증 정보")@RequestHeader String Authorization){
        ResponseDto<List<Request>> responseDto;

        try{
            List<Request> requests = requestService.getStep2Requests(groundId);
            responseDto = ResponseDto.<List<Request>>builder()
                    .code(HttpStatus.OK.value())
                    .message("step2 문서들을 불러왔습니다.")
                    .data(requests)
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(Exception e){
            responseDto = ResponseDto.<List<Request>>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{requestId}")
    @ApiOperation(value="요청 문서 수정")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<Request>> updateRequest(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                              @ApiParam(value = "문서아이디") @PathVariable("requestId") String requestId,
                                                              @ApiParam(value = "title(필수 x), content(필수 x)")@RequestBody RequestUpdateDto requestUpdateDto,
                                                              @ApiParam(value = "인증 정보")@RequestHeader String Authorization,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        ResponseDto<Request> responseDto;

        try{
            Request request = requestService.updateRequest(groundId, requestId, requestUpdateDto, userDetails);
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.OK.value())
                    .message("문서를 수정했습니다.")
                    .data(request)
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(DocumentNotFoundException e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
        }catch(InvalidAttributeValueException e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.UNPROCESSABLE_ENTITY);
        }catch(Exception e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{requestId}/status")
    @ApiOperation(value="요청 문서 진행 상태 변경")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<Void>> changeStatus(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                          @ApiParam(value = "문서아이디")@PathVariable("requestId") String requestId,
                                                          @ApiParam(value = "status -> 0: 해야할 일\n"+
                                                          "status -> 1: 진행 중\n"+
                                                          "status -> 2: 완료")@RequestBody RequestStatusDto requestStatusDto,
                                                          @ApiParam(value = "인증 정보")@RequestHeader String Authorization) {
        ResponseDto<Void> responseDto;

        try{
            requestService.changeStatus(groundId, requestId, requestStatusDto);
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.OK.value())
                    .message("상태를 변경했습니다")
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(DocumentNotFoundException e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
        }catch(InvalidAttributeValueException e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.UNPROCESSABLE_ENTITY);
        }catch(Exception e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{requestId}/sender")
    @ApiOperation(value="요청 문서 보내는 사람 변경")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "유저를 찾을 수 없음"),
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<Void>> changeSender(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                          @ApiParam(value = "문서 아이디")@PathVariable("requestId") String requestId,
                                                          @ApiParam(value = "보내는 사람의 Id")@RequestBody RequestSenderDto requestSenderDto,
                                                          @ApiParam(value = "인증 정보")@RequestHeader String Authorization) {
        ResponseDto<Void> responseDto;

        try{
            requestService.changeSender(groundId, requestId, requestSenderDto);
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.OK.value())
                    .message("보내는 사람을 변경했습니다.")
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(DocumentNotFoundException e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
        }catch(UserNotFoundException e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.FORBIDDEN);
        }catch(InvalidAttributeValueException e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.UNPROCESSABLE_ENTITY);
        }catch(Exception e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{requestId}/receiver")
    @ApiOperation(value="요청 문서 받는 사람 변경")
    @ApiResponses(value = {
            @ApiResponse(code = 403, message = "유저를 찾을 수 없음"),
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<Void>> changeReceiver(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                            @ApiParam(value = "문서 아이디")@PathVariable("requestId") String requestId,
                                                            @ApiParam(value = "받는 사람의 Id")@RequestBody RequestReceiverDto requestReceiverDto,
                                                            @ApiParam(value = "인증 정보")@RequestHeader String Authorization) {
        ResponseDto<Void> responseDto;

        try{
            requestService.changeReceiver(groundId, requestId, requestReceiverDto);
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.OK.value())
                    .message("받는 사람을 변경했습니다.")
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(DocumentNotFoundException e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
        }catch(UserNotFoundException e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.FORBIDDEN.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.FORBIDDEN);
        }catch(InvalidAttributeValueException e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.UNPROCESSABLE_ENTITY);
        }catch(Exception e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{requestId}/createComment")
    @ApiOperation(value="요청 문서 댓글달기")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<Comment>> createComment(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                              @ApiParam(value = "문서아이디")@PathVariable("requestId") String requestId,
                                                              @RequestBody String comment,
                                                              @ApiParam(value = "인증 정보")@RequestHeader String Authorization,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        ResponseDto<Comment> responseDto;

        try{
            Comment saveComment = requestService.createComment(groundId, requestId, comment, userDetails);
            responseDto = ResponseDto.<Comment>builder()
                    .code(HttpStatus.OK.value())
                    .message("댓글을 생성했습니다.")
                    .data(saveComment)
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(DocumentNotFoundException e){
            responseDto = ResponseDto.<Comment>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
        }catch(InvalidAttributeValueException e){
            responseDto = ResponseDto.<Comment>builder()
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.UNPROCESSABLE_ENTITY);
        }catch(Exception e){
            responseDto = ResponseDto.<Comment>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @PutMapping("/{requestId}/connect")
    @ApiOperation(value="요청 문서 카테고리 이동")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<Request>> moveRequest(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                            @ApiParam(value = "문서아이디")@PathVariable("requestId") String requestId,
                                                            @ApiParam(value= "parentId -> 목적지 부모의 아이디") @RequestBody RequestMoveDto requestMoveDto,
                                                            @ApiParam(value = "인증 정보")@RequestHeader String Authorization) {
        ResponseDto<Request> responseDto;

        try{
            Request request = requestService.moveRequest(groundId, requestId, requestMoveDto);
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.OK.value())
                    .message("문서를 이동했습니다.")
                    .data(request)
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(DocumentNotFoundException e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
        }catch(InvalidAttributeValueException e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.UNPROCESSABLE_ENTITY);
        }catch(Exception e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{requestId}")
    @ApiOperation(value="요청 문서 삭제")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<Void>> deleteRequest(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                           @ApiParam(value = "문서아이디")@PathVariable("requestId") String requestId,
                                                           @ApiParam(value = "인증 정보")@RequestHeader String Authorization){

        ResponseDto<Void> responseDto;

        try{
            requestService.deleteRequest(groundId, requestId);
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.OK.value())
                    .message("문서를 삭제했습니다.")
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(DocumentNotFoundException e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
        }catch(InvalidAttributeValueException e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.UNPROCESSABLE_ENTITY);
        }catch(Exception e){
            responseDto = ResponseDto.<Void>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{requestId}/title")
    @ApiOperation(value="요청 문서 제목 수정")
    @ApiResponses(value = {
            @ApiResponse(code = 404, message = "문서 존재하지 않음"),
            @ApiResponse(code = 422, message = "잘못된 요청 데이터"),
            @ApiResponse(code = 500, message = "서버 or 데이터베이스 에러")
    })
    public ResponseEntity<ResponseDto<Request>> updateRequest(@ApiParam(value = "그라운드 아이디")@PathVariable("groundId") int groundId,
                                                              @ApiParam(value = "문서아이디")@PathVariable("requestId") String requestId,
                                                              @RequestBody RequestTitleDto requestTitleDto,
                                                              @ApiParam(value = "인증 정보")@RequestHeader String Authorization,
                                                              @AuthenticationPrincipal UserDetails userDetails) {
        ResponseDto<Request> responseDto;

        try{
            Request request = requestService.titleRequest(groundId, requestId, requestTitleDto, userDetails);
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.OK.value())
                    .message("문서의 제목을 수정했습니다.")
                    .data(request)
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.OK);
        }catch(DocumentNotFoundException e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.NOT_FOUND);
        }catch(InvalidAttributeValueException e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.UNPROCESSABLE_ENTITY.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.UNPROCESSABLE_ENTITY);
        }catch(Exception e){
            responseDto = ResponseDto.<Request>builder()
                    .code(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message(e.getMessage())
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
