import request from './request'
import Service from "./config"


const loginApi = (data,captchaKey) => {
    return request.post({
        //url: '/login?key='+captchaKey,
        url: `/login?key=${captchaKey}`,
        data
    })
}

const registerApi = data => {
    return request.post({
        url: '/register',
        data
    })
}
const sendEmailCode1 = data => {
    return request.post({
        url: '/sendEmail1',
        data
    })
}
const sendEmailCode2 = data => {
  return request.post({
      url: '/sendEmail2',
      data
  })
}
const updatePassword = data => {
    return request.post({
        url: '/updatePassword',
        data
    })
}
const resetPassword = data => {
    return request.post({
        url: '/resetPassword',
        data
    })
}
const searchAll = data => {
    return request.get({
        url: '/searchAll',
        data
    })
}
const updateUserInfo = data => {
    return request.post({
        url: '/updateUserInfo',
        data
    })
}
const logout = data => {
    return request.post({
        url: '/logout',
        data
    })
}
const authApi = data => {
    return request.get({
        url: '/auth',
        data
    })
}
const getMenu = data => {
    return request.post({
        url: '/admin/menu',
        data
    })
}

const getRoles = data => {
    return request.get({
        url: '/getRoles',
        data
    })
}

const editUser = (url,data) => {
    return request.put({
        url,
        data
    })
}
const addUser = data => {
    return request.post({
        url: '/user',
        data
    })
}
const stopUser = data => {
  return request.put({
    url: '/stopUser',
    data
  })
}

const searchDataset = data => {
    return request.get({
        url: '/searchDataset',
        data
    })
}

const addDataset = data =>{
    return request.post({
        url: '/dataset',
        data
    })
}

const editDataset = (url,data) => {
    return request.put({
        url,
        data
    })
}

const uploadDataset = (url, file, data = {}) => {
    const formData = new FormData();
    formData.append('file', file);
    
    // 添加其他表单数据
    Object.keys(data).forEach(key => {
      formData.append(key, data[key]);
    });
  
    // 直接使用原始的Service实例，绕过请求封装
    return Service({
      url,
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data', // 覆盖默认的json类型
        'Authorization': window.sessionStorage.getItem('token') || ''
      }
    });
  }

  const uploadModel = (url, file, data = {}) => {
    const formData = new FormData();
    formData.append('file', file);
    
    // 添加其他表单数据
    Object.keys(data).forEach(key => {
      formData.append(key, data[key]);
    });
  
    // 直接使用原始的Service实例，绕过请求封装
    return Service({
      url,
      method: 'post',
      data: formData,
      headers: {
        'Content-Type': 'multipart/form-data', // 覆盖默认的json类型
        'Authorization': window.sessionStorage.getItem('token') || ''
      }
    });
  }

  const downloadDataset = (userId,datasetId, config = {}) => {
    const url = `/downloadDataset/${datasetId}?user_id=${userId}`;
    return request.get({
      url,
      ...config
    });
  }
  const deleteDataset = (userId, datasetId) => {
    const url = `/deleteDataset/${datasetId}?user_id=${userId}`;
    return request.del({
      url,
    });
  }

  const searchModel = (data) => {
    return request.get({
        url: '/searchModel',
        data
    })
  }

  const editModel = (url,data) => {
    return request.put({
        url,
        data
    })
  }

  const deleteModel = (userId, modelId) => {
    const url = `/deleteModel/${modelId}?user_id=${userId}`;
    return request.del({
      url,
    });
  }

  const downloadModel = (userId, modelId, config = {}) => {
    const url = `/downloadModel/${modelId}?user_id=${userId}`;
    return request.get({
      url,
     ...config
    });
  }

  const searchTask = (data) => {
    return request.get({
        url: '/searchTask',
        data
    })
  }

  const addTask = (data) => {
    return request.post({
        url: '/task',
        data
    })
  }

  const editTask = (url,data) => {
    return request.put({
        url,
        data
    })
  }
  const deleteTask = (userId, taskId) => {
    const url = `/deleteTask/${taskId}?user_id=${userId}`;
    return request.del({
      url,
    });
  }

  const saveArticle = (url,data) => {
    return request.post({
        url,
        data
    })
  }
  const searchArticle = (data) => {
    return request.get({
        url: '/searchArticle',
        data
    })
  }

  const toggleArticlePublic = (url,data) => {
    return request.put({
        url,
        data
    })
  }

  const deleteArticle = (userId, articleId) => {
    const url = `/deleteArticle/${articleId}?user_id=${userId}`;
    return request.del({
      url,
    });
  }
  const getArticle = (url,data) => {
    return request.get({
        url,
        data
    })
  }
  const updateArticle = (data) => {
    return request.put({
        url: '/updateArticle',
        data
    })
  }

  const getAllMenus = (data) => {
    return request.get({
      url: '/getAllMenus',
      data
    })
  }

  const getRoleList = (data) => {
      return request.get({
        url: '/getRoleList',
        data
      })
  }

  const getRoleMenu = (data) => {
      return request.get({
        url: '/getRoleMenu',
        data
      })
  }

  const updateRole = (data) => {
      return request.put({
        url: '/updateRoleMenu',
        data
      })
  }

  const addRole = (data) => {
      return request.post({
        url: '/addRoleMenu',
        data
      })
  }

  const deleteRole = (data) => {
      const url = `/deleteRoleMenu/${data}`;
      return request.del({
        url,
      })
  }

  const segmentImage = (image, model, data = {}) => {
    return request.post({
      url: '/image/segProcess',
      data: {
        image,
        model,
        ...data
      },
      responseType: 'json'
    });
  };

  const classifyImage = (image, model, data = {}) => {
    return request.post({
      url: '/image/clsProcess',
      data: {
        image,
        model,
       ...data
      },
      responseType: 'json'
    });
  }

export default {
    loginApi,
    registerApi,
    resetPassword,
    sendEmailCode1,
    sendEmailCode2,
    searchAll,
    updateUserInfo,
    logout,
    authApi,
    getMenu,
    getRoles,
    editUser,
    addUser,
    stopUser,
    searchDataset,
    addDataset,
    editDataset,
    uploadDataset,
    downloadDataset,
    deleteDataset,
    uploadModel,
    searchModel,
    editModel,
    deleteModel,
    downloadModel,
    searchTask,
    addTask,
    editTask,
    deleteTask,
    saveArticle,
    searchArticle,
    toggleArticlePublic,
    deleteArticle,
    getArticle,
    updateArticle,
    segmentImage,
    classifyImage,
    getAllMenus,
    getRoleMenu,
    updateRole,
    addRole,
    deleteRole,
    getRoleList,
    updatePassword
}