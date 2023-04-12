import axios from "axios";

const axiosApi = axios.create({
  baseURL: "https://ttocket.site/ttocket", //ip 주소
});

export default axiosApi;
