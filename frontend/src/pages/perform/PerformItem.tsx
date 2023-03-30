import { Link } from "react-router-dom";
import axiosApi from "../../services/axiosApi";
import formatDate from "../../components/date/formatDate";
import checkEndDate from "../../components/date/checkEndDate";

import { useLocation } from "react-router-dom";
import { useEffect, useState } from "react";
import BackNav from "../../components/BackNav";
import { useSelector } from "react-redux";
import { RootState } from "../../app/store";

interface performDataType {
  desc: string;
  end_time: string;
  etc: string;
  id: number;
  location: string;
  max_seats: number;
  poster: string;
  price: number;
  start_time: string;
  title: string;
  user_id: string;
}

function PerformItem() {
  const location = useLocation();

  //userID 나중에는 리덕스로 가져올 예정
  const id = useSelector((state: RootState) => state.persistedReducer.user.id);

  // 정보 받은거 내용
  const [isLike, setIsLike] = useState<boolean>(false);
  const [performData, setPerformData] = useState<performDataType>();

  //예매버튼 확인용
  let todayTime = new Date();

  //페이지 뜰 때 데이터 받아오기
  const performDataHandler = async () => {
    try {
      const res = await axiosApi.get(`performance/${id}/${location.state}`, {
        headers: {},
      });
      // console.log(res);
      setIsLike(res.data.body.is_user_like);
      setPerformData(res.data.body.performance_dto);
      console.log(res.data);
    } catch (err) {
      console.log(err);
    }
  };

  //좋아요 버튼 누르기 통신
  const isLikeHandler = async () => {
    try {
      const res = await axiosApi.put(
        `performance/like/${id}/${location.state}`
      );
      setIsLike(res.data.body);
      console.log(res);
    } catch (err) {
      console.log(err);
    }
  };

  useEffect(() => {
    performDataHandler();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <div className="flex flex-col content-center">
      <BackNav  url={"/perform"}/>
      <div className="overflow-scroll mb-20">
        {performData && (
          <div>
            <img
              src={`https://ipfs.io/ipfs/${performData.poster}`}
              // src={performData.poster}
              alt="backposter"
              className="relative w-full h-96 blur-md"
            ></img>
            <div className="flex justify-center w-full">
              <img
                src={`https://ipfs.io/ipfs/${performData.poster}`}
                // src={performData.poster}
                alt="poster"
                className="absolute object-center h-80 w-72 top-40"
              ></img>
            </div>
          </div>
        )}
        <div className="p-2">
          <p className="my-3 text-2xl font-bold">{performData?.title}</p>
          <hr className="my-4 bg-gray-400"></hr>
          <p className="flex justify-between ">
            <span className="mr-2">
              {performData?.end_time.slice(0, 10)}{" "}
              <span className="text-gray-300">|</span>{" "}
              {performData?.end_time.slice(11, 16)} 공연
            </span>

            <span className="flex text-right text-gray-500">
              <svg
                xmlns="http://www.w3.org/2000/svg"
                viewBox="0 0 20 20"
                fill="currentColor"
                className="w-5 h-5 mr-1"
              >
                <path
                  fillRule="evenodd"
                  d="M9.69 18.933l.003.001C9.89 19.02 10 19 10 19s.11.02.308-.066l.002-.001.006-.003.018-.008a5.741 5.741 0 00.281-.14c.186-.096.446-.24.757-.433.62-.384 1.445-.966 2.274-1.765C15.302 14.988 17 12.493 17 9A7 7 0 103 9c0 3.492 1.698 5.988 3.355 7.584a13.731 13.731 0 002.273 1.765 11.842 11.842 0 00.976.544l.062.029.018.008.006.003zM10 11.25a2.25 2.25 0 100-4.5 2.25 2.25 0 000 4.5z"
                  clipRule="evenodd"
                />
              </svg>
              {performData?.location}
            </span>
          </p>
          <p className="mt-8 mb-2 text-lg font-bold">공연 상세</p>
          <p>{performData?.desc}</p>
        </div>
      </div>
      <div className="fixed bottom-0 flex items-center justify-center w-screen pt-2 pb-4 bg-white border-t-2">
        {isLike ? (
          <svg
            xmlns="http://www.w3.org/2000/svg"
            viewBox="0 0 24 24"
            fill="currentColor"
            className="mr-2 w-9 h-9 text-ttokPink"
            onClick={isLikeHandler}
          >
            <path d="M11.645 20.91l-.007-.003-.022-.012a15.247 15.247 0 01-.383-.218 25.18 25.18 0 01-4.244-3.17C4.688 15.36 2.25 12.174 2.25 8.25 2.25 5.322 4.714 3 7.688 3A5.5 5.5 0 0112 5.052 5.5 5.5 0 0116.313 3c2.973 0 5.437 2.322 5.437 5.25 0 3.925-2.438 7.111-4.739 9.256a25.175 25.175 0 01-4.244 3.17 15.247 15.247 0 01-.383.219l-.022.012-.007.004-.003.001a.752.752 0 01-.704 0l-.003-.001z" />
          </svg>
        ) : (
          <svg
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            strokeWidth={1.5}
            stroke="currentColor"
            className="mr-2 text-gray-400 w-9 h-9"
            onClick={isLikeHandler}
          >
            <path
              strokeLinecap="round"
              strokeLinejoin="round"
              d="M21 8.25c0-2.485-2.099-4.5-4.688-4.5-1.935 0-3.597 1.126-4.312 2.733-.715-1.607-2.377-2.733-4.313-2.733C5.1 3.75 3 5.765 3 8.25c0 7.22 9 12 9 12s9-4.78 9-12z"
            />
          </svg>
        )}
        {performData &&
        checkEndDate(
          performData.start_time.slice(0, 10),
          formatDate(todayTime)
        ) ? (
          <button className="h-10 font-bold text-white bg-gray-300 rounded w-80">
            {performData.start_time.slice(0, 10)}{" "}
            {performData?.end_time.slice(11, 16)} 오픈 예정
          </button>
        ) : (
          <Link to="/reserve/" state={location.state}>
            <button className="bg-[#FB7185] text-white w-80 h-10 rounded font-bold">
              예매하기
            </button>
          </Link>
        )}
      </div>
    </div>
  );
}

export default PerformItem;
