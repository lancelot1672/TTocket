import React from 'react'
import { Link } from 'react-router-dom'

export default function BottomNav() {
  return (
    <div className='grid grid-cols-3 gap-4 mt-4'>
        <Link to="/home/reserve">
            <div className="col-span-1 bg-gray-200">
                <button>티켓 예매</button>
            </div>
        </Link>
        <Link to="/home">
            <div className="col-span-1 bg-gray-200">
                <button >홈</button>
            </div>
        </Link>
        <Link to="/home/box">
            <div className="col-span-1 bg-gray-200">
                <button>보관함</button>
            </div>
        </Link>        
        
    </div>
  )
}
