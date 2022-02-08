package edu.kosmo.ex.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.google.gson.Gson;

import edu.kosmo.ex.vo.kakao.KakaoAuth;
import edu.kosmo.ex.vo.kakao.KakaoProfile;

//@Resource�� �����Ϸ��� �ϴ� ��ü�� �̸�(id)�� ��ġ�ϴ� ��ü�� �ڵ����� �����Ѵ�.

@Service
public class KakaoServiceImpl {

	 private final static String K_CLIENT_ID = "d367f2f3dacfeafe2113e96d64623b88";
	 private final static String K_REDIRECT_URI = "http://localhost:8282/ex/auth/kakao/callback";
	 

	public String getAuthorizationUrl() {
		String kakaoUrl = "https://kauth.kakao.com/oauth/authorize?" + "client_id=" + K_CLIENT_ID + "&redirect_uri="
				+ K_REDIRECT_URI + "&response_type=code";
		return kakaoUrl;
	}

	// POST������� key-value �����͸� ��û(īī��������)
	private final static String K_TOKEN_URI = "https://kauth.kakao.com/oauth/token";

	// ��ū �ޱ� ����
	// https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#request-token
	// public RetKakaoAuth getKakaoTokenInfo(String code) {
	public KakaoAuth getKakaoTokenInfo(String code) {
		RestTemplate restTemplate = new RestTemplate();// http ��û�� �����ϰ� ���� �� �ִ� Ŭ����
		// Set header : Content-type: application/x-www-form-urlencoded
		HttpHeaders headers = new HttpHeaders();
		// headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		// Set parameter
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", K_CLIENT_ID);
		params.add("redirect_uri", K_REDIRECT_URI);
		params.add("code", code);

		// Set http entity
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		// ������ ��û�ϱ�
		// Http ��û�ϱ� - POST ������� - �׸��� response ������ ������ ����.
		ResponseEntity<String> response = restTemplate.postForEntity(K_TOKEN_URI, kakaoTokenRequest,
				String.class);

		System.out.println(response.getBody());
		Gson gson = new Gson();
		//Gson ,Json Simple, ObjectMapper ������ ������ Ŭ������ ����
		if (response.getStatusCode() == HttpStatus.OK) {
			return gson.fromJson(response.getBody(), KakaoAuth.class); 
		}
		
		return null;

	}
	
	private final static String K_PROFILE_URI = "https://kapi.kakao.com/v2/user/me";
	// �������� �ޱ�(��ū�� �޾����� �ش� ��ū���� ���ҽ� ����)
	//https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info
	public KakaoProfile getKakaoProfile(String accessToken) {
		
		RestTemplate restTemplate = new RestTemplate();// http ��û�� �����ϰ� ���� �� �ִ� Ŭ����
        
		// Set header : Content-type: application/x-www-form-urlencoded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);
        
        // Set http entity
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
		Gson gson = new Gson();
        try {
            // Request profile
            ResponseEntity<String> response = restTemplate.postForEntity(K_PROFILE_URI, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK)
            	System.out.println(response.getBody());
            	KakaoProfile profile = gson.fromJson(response.getBody(), KakaoProfile.class);
            	System.out.println(profile);
            	return gson.fromJson(response.getBody(), KakaoProfile.class);
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
	
	//�α׾ƿ�
	//�α׾ƿ� API�� ����� �׼��� ��ū�� �������� ��ū�� ��� �����ŵ�ϴ�
	//https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
	private final static String K_LOGOUT_URI = "https://kapi.kakao.com/v1/user/logout";
	public String kakaoLogout(String accessToken) {
		
		RestTemplate restTemplate = new RestTemplate();// http ��û�� �����ϰ� ���� �� �ִ� Ŭ����
        
		// Set header : Content-type: application/x-www-form-urlencoded
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.set("Authorization", "Bearer " + accessToken);
        
        //Post ����
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, headers);
		Gson gson = new Gson();
        try {
            // Request profile
            ResponseEntity<String> response = restTemplate.postForEntity(K_LOGOUT_URI, request, String.class);
            
            if (response.getStatusCode() == HttpStatus.OK)
            	System.out.println(response.getBody());

            	return "SUCESS";
            
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return null;
    }
	
	//īī�������� �Բ� �α׾ƿ�
	//https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api
	private final static String K_LOGOUT_WITH_ID_URI = "https://kauth.kakao.com/oauth/logout";
	private final static String K_LOGOUT_CALLBACK_URI = "http://localhost:8282/ex/kakao/logout";
		
	public String kakaoLogoutWithID(String code) {
		RestTemplate restTemplate = new RestTemplate();// http ��û�� �����ϰ� ���� �� �ִ� Ŭ����
		// Set header : Content-type: application/x-www-form-urlencoded
		HttpHeaders headers = new HttpHeaders();
		// headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");
		// Set parameter
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("grant_type", "authorization_code");
		params.add("client_id", K_CLIENT_ID);
		params.add("logout_redirect_uri", K_LOGOUT_CALLBACK_URI);
		params.add("code", code);

		// Set http entity
		HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

		// ������ ��û�ϱ�
		// Http ��û�ϱ� - POST ������� - �׸��� response ������ ������ ����.
		ResponseEntity<String> response = restTemplate.postForEntity(K_LOGOUT_WITH_ID_URI, kakaoTokenRequest,
				String.class);

		System.out.println(response.getBody());
		Gson gson = new Gson();
		//Gson ,Json Simple, ObjectMapper ������ ������ Ŭ������ ����
		if (response.getStatusCode() == HttpStatus.OK) {
			return gson.fromJson(response.getBody(), String.class); 
		}
		
		return null;

	}
	

}
