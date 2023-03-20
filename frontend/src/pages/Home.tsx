import React from 'react'
import { Route, Routes } from 'react-router-dom'
import Box from './box/Box'
import Reserve from './reserve/Reserve'
import Ticket from './ticket/Ticket'
import HeaderNav from '../components/HeaderNav'
import BottomNav from '../components/BottomNav'

function Home() {
  return (
    <div>
        <HeaderNav/>
        <Routes>
            <Route path="/*" element={<Ticket />} />
            <Route path="/reserve/*" element={<Reserve />} />
            <Route path="/box/*" element={<Box />} />
            {/* 상단에 위치하는 라우트들의 규칙을 모두 확인, 일치하는 라우트가 없는경우 처리 */}
        </Routes>
        <BottomNav/>
    </div>
  )
}

export default Home