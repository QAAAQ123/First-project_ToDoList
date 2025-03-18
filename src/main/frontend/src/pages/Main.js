import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import axios from 'axios';
import './Main.css'; // CSS 파일 임포트

function Main() {
  const [pages, setPages] = useState([]);
  const [newPageTitle, setNewPageTitle] = useState('');
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPages = async () => {
      try {
        const response = await axios.get('/pages');
        setPages(response.data);
      } catch (err) {
        setError('페이지 데이터를 불러오는데 실패했습니다.');
        console.error(err);
      }
    };
    fetchPages();
  }, []);

  const handleCreatePage = async () => {
    if (!newPageTitle.trim()) {
      setError('페이지 제목을 입력하세요.');
      return;
    }
    try {
      const response = await axios.post('/pages', {
        title: newPageTitle,
      });
      setPages([...pages, response.data]);
      setNewPageTitle('');
      setError(null);
    } catch (err) {
      setError('페이지 생성에 실패했습니다.');
      console.error(err);
    }
  };

  const handleUpdatePage = async (pageId, currentTitle) => {
    const newTitle = prompt('새 제목을 입력하세요:', currentTitle);
    if (!newTitle || newTitle === currentTitle) return;

    try {
     const response = await axios.patch(`/pages/${pageId}`, {
        id: pageId,
        title: newTitle,
      });
      setPages(pages.map((page) => (page.id === pageId ? response.data : page)));
      setError(null);
    } catch (err) {
      setError('페이지 수정에 실패했습니다.');
      console.error(err);
    }
  };

  const handleDeletePage = async (pageId) => {
    if (!window.confirm('정말 삭제하시겠습니까?')) return;

    try {
      await axios.delete(`/pages/${pageId}`);
      setPages(pages.filter((page) => page.id !== pageId));
      setError(null);
    } catch (err) {
      setError('페이지 삭제에 실패했습니다.');
      console.error(err);
    }
  };

  if (error) return <div className="error">{error}</div>;
  if (pages.length === 0) return <div className="loading">로딩 중...</div>;

  return (
    <div className="container">
      <h1 className="title">메인 페이지 - 페이지 목록</h1>
      <ul className="todo-list">
        {pages.map((page) => (
          <li key={page.id} className="todo-item">
            <Link to={`/pages/${page.id}`} className="page-link">
              <span>{page.title}</span>
            </Link>
            <div className="button-group">
              <button
                onClick={() => handleUpdatePage(page.id, page.title)}
                className="update-btn"
              >
                수정
              </button>
              <button onClick={() => handleDeletePage(page.id)} className="delete-btn">
                삭제
              </button>
            </div>
          </li>
        ))}
      </ul>

      <div className="create-section">
        <textarea
          value={newPageTitle}
          onChange={(e) => setNewPageTitle(e.target.value)}
          placeholder="새 페이지 제목 입력"
          className="todo-input"
        />
        <button onClick={handleCreatePage} className="create-btn">
          페이지 생성
        </button>
      </div>
    </div>
  );
}

export default Main;