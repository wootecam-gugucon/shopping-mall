# spring-shopping

## 요구 사항

### 1단계 - 상품

- 상품 목록 기능 구현
    - 상품 기본 정보 (상품 ID, 상품 이름, 상품 이미지, 상품 가격)
    - 필요한 경우 상품 정보의 종류를 추가할 수 있다. (ex. 상품 설명, 상품 카테고리)
- 상품 목록 페이지 연동
    - root url로 접근할 경우 상품 목록 페이지를 조회할 수 있다.

### 2단계 - 로그인

- 사용자 도메인 모델 설계
    - 사용자의 기본 정보는 email, password 이다.
    - 필요한 경우 사용자 정보의 종류를 추가할 수 있다. (ex. 닉네임, 주소)
- 로그인 기능 구현
    - 사용자 인증을 구현
    - 인증은 토큰 방식으로 이뤄지며 JWT 토큰을 활용
- 로그인 페이지 연동
    - '/login' url로 접근할 경우 로그인 페이지를 조회할 수 있다.
    - 로그인 후 상품 목록 페이지('/')로 이동한다.
- 예외 처리
    - 아이디가 없을 경우
    - 비밀번호가 틀릴 경우
    - accessToken이 유효하지 않을 경우

### 3단계 - 장바구니

- 장바구니 기능 구현
    - 액세스 토큰을 사용하여 사용자별 장바구니 기능을 구현
        - 사용자 정보 요청 Header의 Authorization 필드를 사용해 인증 처리
            - 인증 방식은 Bearer 인증 사용
            - Authorization : <type> <credentials>
                - type : Bearer
                - credentials : jwt token
    - 장바구니에 상품 상품 추가
    - 장바구니에 담긴 상품 목록 조회
    - 장바구니에 담긴 상품 수량 변경
        - 장바구니 상품 수량이 0개일 경우 장바구니에서 상품 제거
    - 장바구니에 담긴 상품 제거
- 장바구니 페이지 연동
    - /cart url로 접근할 경우 장바구니 페이지를 조회할 수 있다.
    - 장바구니의 상품의 수량을 변경하거나 삭제할 수 있다.
- 예외
    - 장바구니에 상품 추가 시, 이미 존재할 경우
    - 장바구니 상품이 0개 미만이거나 1000개 초과일 경우
    - 내가 추가한 장바구니 상품이 아닐 경우

### API 목록

* `GET /` (상품 목록 페이지 요청)
    * 응답
        * 상품 목록 페이지 (HTML)

* `GET /login` (로그인 페이지 요청)
    * 응답
        * 로그인 페이지 (HTML)

* `POST /login/token` (로그인 요청)
    * 요청
        * 본문 (JSON)
            * email: String, password: String
    * 응답
        * 본문 (JSON)
            * accessToken: String

* `GET /cart` (장바구니 페이지 요청)
    * 응답
        * 장바구니 페이지 (HTML)

* `POST /cart/items` (장바구니 상품 추가 요청)
    * 요청
        * 헤더
            * Authorization: Bearer ${ACCESS_TOKEN}
        * 본문 (JSON)
            * productId: Long
    * 응답
        * 없음 (성공 시 200)

* `GET /cart/items` (장바구니 조회 요청)
    * 요청
        * 헤더
            * Authorization: Bearer ${ACCESS_TOKEN}
    * 응답
        * 본문 (JSON)
            * CartItemResponse의 리스트
                * CartItemResponse
                    * cartItemId: Long
                    * name: String
                    * imageFileName: String
                    * price: int
                    * quantity: int

* `PUT /cart/items/{cartItemId}/quantity` (장바구니 상품 수량 수정 요청)
    * 요청
        * 헤더
            * Authorization: Bearer ${ACCESS_TOKEN}
        * 파라미터
            * cartItemId: Long
        * 본문 (JSON)
            * quantity: int
    * 응답
        * 없음 (성공 시 200)

* `DELETE /cart/items/{cartItemId}` (장바구니 상품 삭제 요청)
    * 요청
        * 헤더
            * Authorization: Bearer ${ACCESS_TOKEN}
        * 파라미터
            * cartItemId: Long
    * 응답
        * 없음 (성공 시 204)
