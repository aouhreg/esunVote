
import axios from 'axios'

var http = axios.create({
  baseURL: '/api',          
  timeout: 10000,           
  headers: {                
    'Content-Type': 'application/json'
  }
})

http.interceptors.response.use(
  function(response) {
    return response.data
  },

  function(error) {
    var message = ''

    if (error.response && error.response.data && error.response.data.message) {
      message = error.response.data.message
    }
    else if (error.message) {
      message = error.message
    }
    else {
      message = '網路錯誤'
    }

    return Promise.reject(new Error(message))
  }
)
// voteApi.xxxx() 呼叫對應的 API 方法
var voteApi = {
  // 取得所有投票項目與票數
  // GET /api/items
  getItems: function() {
    return http.get('/items')
  },

  // 新增一個投票項目
  // POST /api/items
  createItem: function(name) {
    return http.post('/items', { name: name })
  },

  // 修改投票項目名稱
  // PUT /api/items/{id}
  updateItem: function(id, name) {
    return http.put('/items/' + id, { name: name })
  },

  // 刪除投票項目
  // DELETE /api/items/{id}
  deleteItem: function(id) {
    return http.delete('/items/' + id)
  },

  // 提交投票（可以投多個項目）
  // POST /api/votes
  castVotes: function(itemIds, voter) {
    return http.post('/votes', { itemIds: itemIds, voter: voter })
  }
}

export { voteApi }
