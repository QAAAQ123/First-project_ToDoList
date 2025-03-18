import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Main from './pages/Main';
import Todo from './pages/Todo';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="pages" element={<Main />} />
        <Route path="pages/:pageId" element={<Todo />} />
      </Routes>
    </Router>
  );
}

export default App;