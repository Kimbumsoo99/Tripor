package com.tripor.article.controller;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tripor.article.model.dto.ArticleDto;
import com.tripor.article.model.dto.ArticleListDto;
import com.tripor.article.model.dto.ArticlePostDto;
import com.tripor.article.model.dto.CommentDto;
import com.tripor.article.model.dto.FileInfoDto;
import com.tripor.article.model.service.ArticleService;
import com.tripor.member.model.dto.MemberDto;

import io.jsonwebtoken.lang.Collections;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin("*")
@RestController
@RequestMapping("/article")
@Tag(name = "게시물")
public class ArticleController {

	@Value("${file.path}")
	private String uploadPath;

	@Value("${file.path.upload-images}")
	private String uploadImagePath;

	@Value("${file.path.upload-files}")
	private String uploadFilePath;

	@Autowired
	ArticleService articleService;

	@Operation(summary = "전체 게시물 목록 조회")
	@ApiResponses(value = { @ApiResponse(responseCode = "200", description = "OK"),
			@ApiResponse(responseCode = "404", description = "None Page"),
			@ApiResponse(responseCode = "500", description = "Server Error") })
	@GetMapping("")
	public ResponseEntity<?> listArticle(
			@RequestParam @Parameter(description = "게시글을 얻기위한 부가정보", required = true) Map<String, String> map) {
		log.debug("listArticle map : {}", map);
		try {
			// pgno, key, word
			ArticleListDto list = articleService.listArticle(map);

			Map<String, Object> returnMap = new HashMap<>();
			returnMap.put("items", list);
			returnMap.put("meta", list.getPageNavigation());

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
			return ResponseEntity.ok().headers(headers).body(returnMap);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	@Operation(summary = "게시물 조회")
	@GetMapping("/{articleId}")
	public ResponseEntity<?> getArticle(@PathVariable("articleId") int articleId) {
		log.debug("getArticle articleId : {}", articleId);
		try {
			// pgno, key, word
			ArticleDto articleDto = articleService.getArticle(articleId);

			Map<String, Object> returnMap = new HashMap<>();
			returnMap.put("item", articleDto);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
			return ResponseEntity.ok().headers(headers).body(returnMap);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	@Operation(summary = "게시물 삭제")
	@DeleteMapping("/{articleId}")
	public ResponseEntity<?> deleteArticle(@PathVariable("articleId") int articleId) {
		log.debug("deleteArticle articleId : {}", articleId);
		try {
			// pgno, key, word
			articleService.deleteArticle(articleId);

			Map<String, Object> returnMap = new HashMap<>();
			returnMap.put("result", "ok");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
			return ResponseEntity.ok().headers(headers).body(returnMap);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	@Operation(summary = "조회수 업데이트")
	@PutMapping("/hit/{articleId}")
	public ResponseEntity<?> updateHit(@PathVariable("articleId") int articleId) {
		log.debug("updateHit articleId : {}", articleId);
		try {
			// pgno, key, word
			articleService.updateHit(articleId);

			Map<String, Object> returnMap = new HashMap<>();
			returnMap.put("item", articleService.getArticle(articleId));

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
			return ResponseEntity.ok().headers(headers).body(returnMap);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	@Operation(summary = "게시물 수정")
	@PutMapping("")
	public ResponseEntity<?> modifyArticle(@org.springframework.web.bind.annotation.RequestBody ArticleDto articleDto) {
		log.debug("modifyArticle articleDto : {}", articleDto);
		try {
			// pgno, key, word
			articleService.modifyArticle(articleDto);

			Map<String, Object> returnMap = new HashMap<>();
			returnMap.put("item", articleService.getArticle(articleDto.getArticleId()));

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(new MediaType("application", "json", StandardCharsets.UTF_8));
			return ResponseEntity.ok().headers(headers).body(returnMap);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	@Operation(summary = "게시물 작성")
	@PostMapping(value = "")
	public ResponseEntity<?> writeArticle(
			@org.springframework.web.bind.annotation.RequestBody ArticlePostDto articlePostDto) {
		log.debug("writeArticle requestBody : {}", articlePostDto);
		try {
			int articleId = articleService.writeArticle(articlePostDto);
			// 응답으로 보낼 JSON 데이터 구성
			Map<String, Object> response = new HashMap<>();
			response.put("result", "ok");
			response.put("data", articleId);

			// 응답 반환
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}
//	@Operation(summary = "게시물 작성")
//	@PostMapping(value = "")
//	public ResponseEntity<?> writeArticle(@RequestPart(name = "article") @Valid ArticleDto articleDto,
//			@RequestPart(name = "images", required = false) MultipartFile[] images) {
//		log.debug("writeArticle articleDto : {}", articleDto);
//		try {
//			if (images != null && !images[0].isEmpty()) {
//				log.debug("uploadPath : {}, uploadImagePath : {}, uploadFilePath : {}", uploadPath, uploadImagePath,
//						uploadFilePath);
//				String today = new SimpleDateFormat("yyMMdd").format(new Date());
//				String saveFolder = uploadPath + File.separator + today;
//				log.debug("저장 폴더 : {}", saveFolder);
//				File folder = new File(saveFolder);
//				if (!folder.exists())
//					folder.mkdirs();
//				List<FileInfoDto> fileInfos = new ArrayList<FileInfoDto>();
//				for (MultipartFile mfile : images) {
//					FileInfoDto fileInfoDto = new FileInfoDto();
//					String originalFileName = mfile.getOriginalFilename();
//					if (!originalFileName.isEmpty()) {
//						String saveFileName = UUID.randomUUID().toString()
//								+ originalFileName.substring(originalFileName.lastIndexOf('.'));
//						fileInfoDto.setSaveFolder(today);
//						fileInfoDto.setOriginalFile(originalFileName);
//						fileInfoDto.setSaveFile(saveFileName);
//						log.debug("원본 파일 이름 : {}, 실제 저장 파일 이름 : {}", mfile.getOriginalFilename(), saveFileName);
//						mfile.transferTo(new File(folder, saveFileName));
//					}
//					fileInfos.add(fileInfoDto);
//				}
//				articleDto.setFileInfos(fileInfos);
//			}
//			articleService.writeArticle(articleDto);
//			// 응답으로 보낼 JSON 데이터 구성
//			Map<String, Object> response = new HashMap<>();
//			response.put("response", "ok");
//			response.put("data", articleDto.getArticleId());
//
//			// 응답 반환
//			return new ResponseEntity<>(response, HttpStatus.CREATED);
//		} catch (Exception e) {
//			return exceptionHandling(e);
//		}
//	}

	@Operation(summary = "이미지 저장")
	@PostMapping(value = "/image")
	public ResponseEntity<?> insertImage(@RequestPart(name = "image", required = false) MultipartFile image) {
		log.debug("writeArticle articleDto : {}", image);

		System.out.println(image.isEmpty());
		System.out.println(image.getContentType());
		System.out.println(image.getOriginalFilename());
		System.out.println(image.getName());
		System.out.println(image.getSize());
		try {
			System.out.println(image.getBytes());
			System.out.println(image.getInputStream());
		} catch (IOException e) {
			System.out.println("에러");
		}
		System.out.println(image.getResource());
		System.out.println("-------------------------------------------------------------");

		try {
			if (image != null && !image.isEmpty()) {
				log.debug("uploadPath : {}, uploadImagePath : {}, uploadFilePath : {}", uploadPath, uploadImagePath,
						uploadFilePath);
				String today = new SimpleDateFormat("yyMMdd").format(new Date());
				String saveFolder = uploadPath + File.separator + today;
				log.debug("저장 폴더 : {}", saveFolder);
				File folder = new File(saveFolder);
				if (!folder.exists())
					folder.mkdirs();
				FileInfoDto fileInfoDto = new FileInfoDto();
				String originalFileName = image.getOriginalFilename();
				if (!originalFileName.isEmpty()) {
					String saveFileName = UUID.randomUUID().toString()
							+ originalFileName.substring(originalFileName.lastIndexOf('.'));
					fileInfoDto.setSaveFolder(today);
					fileInfoDto.setOriginalFile(originalFileName);
					fileInfoDto.setSaveFile(saveFileName);
					log.debug("원본 파일 이름 : {}, 실제 저장 파일 이름 : {}", image.getOriginalFilename(), saveFileName);
					log.info("image - {}", fileInfoDto);
					image.transferTo(new File(folder, saveFileName));
				}
				FileInfoDto registed = articleService.registerImage(fileInfoDto);
				// 응답으로 보낼 JSON 데이터 구성
				Map<String, Object> response = new HashMap<>();
				response.put("response", "ok");
				response.put("fileInfo", registed);
//				response.put("data", articleDto.getArticleId());
				return new ResponseEntity<>(response, HttpStatus.CREATED);
			} else {
				throw new Exception("이미지 누락");
			}
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	@Operation(summary = "이미지 삭제")
	@DeleteMapping(value = "/image/{imageId}")
	public ResponseEntity<?> deleteImage(@PathVariable String imageId) {
		log.debug("imageId : {}", imageId);
		try {
			articleService.deleteImage(Integer.parseInt(imageId));
			// 응답으로 보낼 JSON 데이터 구성
			Map<String, Object> response = new HashMap<>();
			response.put("response", "ok");
//				response.put("data", articleDto.getArticleId());
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (Exception e) {
			return exceptionHandling(e);
		}
	}

	// 댓글 추가
	@PostMapping("/{articleId}/comment")
	public ResponseEntity<?> addComment(@PathVariable int articleId,
			@org.springframework.web.bind.annotation.RequestBody CommentDto commentDto) {
		commentDto.setArticleId(articleId);
		log.info("댓글 - {}", commentDto);
		try {
			articleService.addComment(commentDto);

			Map<String, Object> response = new HashMap<>();
			response.put("result", "ok");
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return exceptionHandling(e);
		}
	}

	// 특정 게시글의 모든 댓글 가져오기
	@GetMapping("/{articleId}/comments")
	public ResponseEntity<?> getComments(@PathVariable int articleId) {
		try {
			List<CommentDto> commentDtos = articleService.getCommentsByArticleId(articleId);

			java.util.Collections.sort(commentDtos, (o1, o2) -> {
				return o1.getCommentId() - o2.getCommentId();
			});

			Map<Integer, CommentDto> comments = new HashMap<>();

			for (CommentDto comment : commentDtos) {
				if (comment.getParentCommentId() == null) {
					comments.put(comment.getCommentId(), comment);
				} else {
					comments.get(comment.getParentCommentId()).getChildComments().add(comment);
				}
			}

			List<CommentDto> returnComments = new ArrayList<>();
			for (int commentId : comments.keySet()) {
				returnComments.add(comments.get(commentId));
			}
			java.util.Collections.sort(returnComments, (o1, o2) -> {
				return o1.getCommentId() - o2.getCommentId();
			});

			Map<String, Object> response = new HashMap<>();
			response.put("result", "ok");
			response.put("items", returnComments);
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return exceptionHandling(e);
		}
	}

	// 댓글 수정
	@PutMapping("/{articleId}/comments/{commentId}")
	public ResponseEntity<?> updateComment(@PathVariable int articleId, @PathVariable int commentId,
			@org.springframework.web.bind.annotation.RequestBody CommentDto commentDto) {
		commentDto.setCommentId(commentId);
		commentDto.setArticleId(articleId);
		try {
			articleService.updateComment(commentDto);

			Map<String, Object> response = new HashMap<>();
			response.put("result", "ok");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return exceptionHandling(e);
		}
	}

	// 댓글 삭제
	@DeleteMapping("/{articleId}/comments/{commentId}")
	public ResponseEntity<?> deleteComment(@PathVariable int articleId, @PathVariable int commentId) {
		try {
			articleService.deleteComment(commentId);

			Map<String, Object> response = new HashMap<>();
			response.put("result", "ok");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			return exceptionHandling(e);
		}
	}

	private ResponseEntity<String> exceptionHandling(Exception e) {
		e.printStackTrace();
//		return new ResponseEntity<String>("Error : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error : " + e.getMessage());
	}
}
