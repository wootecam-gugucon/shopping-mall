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

### 4단계 - 주문

- 주문 기능 구현
    - 기능 목록
        - 장바구니에 담긴 아이템 전체 주문
        - 특정 주문의 상세 정보를 확인
        - 사용자별 주문 목록 확인
    - 사용자 정보는 요청 header의 Authorization 필드를 사용해 인증 처리를 해서 얻는다.
    - 주문 기본 정보
        - 주문 번호
        - 주문 아이템 정보
            - 이름, 가격, 이미지, 수량
        - 총 결제금액
    - 필요한 경우 주문 정보의 종류를 추가할 수 있다. (ex. 주문 시간, 상태)
- 주문 페이지 연동
    - 장바구니 목록 페이지(`/cart`)에서 주문하기 버튼을 통해 장바구니에 담은 아이템을 주문할 수 있다.
    - 주문 요청이 성공하면 주문 상세 페이지로 이동한다.
- 사용자별 주문 목록 확인
    - /order-history url로 접근할 경우 주문 목록 페이지를 조회할 수 있다.
    - 상세보기 버튼을 클릭해 주문 상세 정보 페이지로 이동할 수 있다.
- 예외
    - 주문 요청 시 장바구니가 비어 있는 경우
    - 존재하지 않는 주문을 조회하는 경우
    - 다른 사용자의 주문을 조회하는 경우

### 5단계 - 주문 (환율 적용)

- 주문 시점의 실시간 환율 저장
    - 실시간 환율 정보는 `https://currencylayer.com/` 의 API를 사용해서 조회
- 주문 관련 페이지에 적용 환율, 변환 금액 반영
    - 4단계에서 작업한 주문 관련 페이지에 총 주문 금액과 함께 적용 환율, 변환된 총 주문 금액을 함께 노출하도록 변경

### API 목록

- `GET /` (상품 목록 페이지 요청)
    - 응답
        - 상품 목록 페이지 (HTML)

- `GET /login` (로그인 페이지 요청)
    - 응답
        - 로그인 페이지 (HTML)

- `POST /api/v1/login/token` (로그인 API 요청)
    - 요청
        - 본문 (JSON)
            - email: String, password: String
    - 응답
        - 본문 (JSON)
            - accessToken: String

- `GET /cart` (장바구니 페이지 요청)
    - 응답
        - 장바구니 페이지 (HTML)

- `POST /api/v1/cart/items` (장바구니 상품 추가 API 요청)
    - 요청
        - 헤더
            - Authorization: Bearer ${ACCESS_TOKEN}
        - 본문 (JSON)
            - productId: Long
    - 응답
        - 없음 (성공 시 200)

- `GET /api/v1/cart/items` (장바구니 조회 API 요청)
    - 요청
        - 헤더
            - Authorization: Bearer ${ACCESS_TOKEN}
    - 응답
        - 본문 (JSON)
            - CartItemResponse의 리스트
                - CartItemResponse
                    - cartItemId: Long
                    - name: String
                    - imageFileName: String
                    - price: int
                    - quantity: int

- `PUT /api/v1/cart/items/{cartItemId}/quantity` (장바구니 상품 수량 수정 API 요청)
    - 요청
        - 헤더
            - Authorization: Bearer ${ACCESS_TOKEN}
        - 파라미터
            - cartItemId: Long
        - 본문 (JSON)
            - quantity: int
    - 응답
        - 없음 (성공 시 200)

- `DELETE /api/v1/cart/items/{cartItemId}` (장바구니 상품 삭제 API 요청)
    - 요청
        - 헤더
            - Authorization: Bearer ${ACCESS_TOKEN}
        - 파라미터
            - cartItemId: Long
    - 응답
        - 없음 (성공 시 204)

- `POST /api/v1/order` (주문 API 요청)
    - 요청
        - 헤더
            - Authorization: Bearer ${ACCESS_TOKEN}
    - 응답
        - 헤더
            - Location: `/order/{orderId}` (생성된 주문의 상세조회 페이지)

- `GET /order/{orderId}` (주문 상세조회 페이지 요청)
    - 응답
        - 주문 상세조회 페이지 (HTML)

- `GET /api/v1/order/{orderId}` (주문 상세조회 API 요청)
    - 요청
        - 헤더
            - Authorization: Bearer ${ACCESS_TOKEN}
        - 파라미터
            - orderId: Long
    - 응답
        - 본문 (JSON)
            - OrderDetailResponse
                - orderId: Long
                - OrderItemResponse의 리스트
                    - OrderItemResponse
                        - cartItemId: Long
                        - name: String
                        - imageFileName: String
                        - price: Long
                        - quantity: Integer
                - totalPrice: Long

- `GET /order-history` (주문목록 페이지 요청)
    - 응답
        - 주문목록 페이지 (HTML)

- `GET /api/v1/order-history` (주문 목록 정보 API 요청)
    - 요청
        - 헤더
            - Authorization: Bearer ${ACCESS_TOKEN}
    - 응답
        - 본문 (JSON)
            - OrderItemResponse의 리스트
