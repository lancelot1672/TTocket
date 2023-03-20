import axios from 'axios';

const axiosApi = axios.create({
    baseURL : '스프링 서버 주소 나오면 입력할 예정', //ip 주소
})

export default axiosApi;