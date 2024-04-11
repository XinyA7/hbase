import axios from 'axios'
axios.defaults.baseURL = ''


const service = axios.create();


service.interceptors.request.use(
  config => {
    config.headers['Content-Type'] = "application/json "
    return config
  },
  error => {
    console.log(error) 
    return Promise.reject(error)
  }
)

service.interceptors.response.use(
  response => {
    return response.data
  },
  error => {
    console.log('err' + error) 
    return Promise.reject(error)
  }
)

export default service
