import React, { useState, useEffect } from 'react';
import { useParams, Link } from 'react-router-dom'; // Link 추가
import axios from 'axios';
import './Todo.css';

function Todo() {

  const { pageId } = useParams();
  const [page, setPage] = useState(null);
  const [newTodoContent, setNewTodoContent] = useState('');
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchPage = async () => {
      try {
        const response = await axios.get(`/pages/${pageId}`);
        setPage(response.data);
      } catch (err) {
        setError('Todo 데이터를 불러오는데 실패했습니다.');
        console.error(err);
      }
    };
    fetchPage();
  }, [pageId]);

  const handleCreateTodo = async () => {
    if (!newTodoContent.trim()) {
      setError('Todo 내용을 입력하세요.');
      return;
    }
    try {
      const response = await axios.post(`/pages/${pageId}`, {
        content: newTodoContent,
      });
      setPage(response.data);
      setNewTodoContent('');
      setError(null);
    } catch (err) {
      setError('Todo 생성에 실패했습니다.');
      console.error(err);
    }
  };

  const handleUpdateTodo = async (toDoId, currentContent) => {
    const newContent = prompt('새 내용을 입력하세요:', currentContent);
    if (!newContent || newContent === currentContent) return;

    try {
      const response = await axios.patch(`/pages/${pageId}/${toDoId}`, {
        id: toDoId,
        pageId: pageId,
        content: newContent,
      });
      setPage(response.data);
      setError(null);
    } catch (err) {
      setError('Todo 수정에 실패했습니다.');
      console.error(err);
    }
  };

  const handleDeleteTodo = async (toDoId) => {
    if (!window.confirm('정말 삭제하시겠습니까?')) return;

    try {
      const response = await axios.delete(`/pages/${pageId}/${toDoId}`);
      setPage(response.data);
      setError(null);
    } catch (err) {
      setError('Todo 삭제에 실패했습니다.');
      console.error(err);
    }
  };

  if (error) return <div className="error">{error}</div>;
  if (!page) return <div className="loading">로딩 중...</div>;

  return (
    <div className="container">
      <h1 className="title">{page.title} - Todo 목록</h1>
      <ul className="todo-list">
        {page.toDoList.map((todo) => (
          <li key={todo.id} className="todo-item">
            <span>{todo.content}</span>
            <div className="button-group">
              <button
                onClick={() => handleUpdateTodo(todo.id, todo.content)}
                className="update-btn"
              >
                수정
              </button>
              <button onClick={() => handleDeleteTodo(todo.id)} className="delete-btn">
                삭제
              </button>
            </div>
          </li>
        ))}
      </ul>

      <div className="create-section">
        <textarea
          value={newTodoContent}
          onChange={(e) => setNewTodoContent(e.target.value)}
          placeholder="새 Todo 내용 입력"
          className="todo-input"
        />
        <button onClick={handleCreateTodo} className="create-btn">
          Todo 생성
        </button>
      </div>

      <Link to="/pages" className="back-link">
        메인 페이지로 돌아가기
      </Link>
    </div>
  );
}

export default Todo;