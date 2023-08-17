/**
 * Copyright School of Informatics Xiamen University
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package cn.edu.xmu.javaee.core.model;

import java.io.Serializable;

public class InternalReturnObject<T> implements Serializable {
    private Integer errno=0;
    private String errmsg="成功";
    private T data;
    public InternalReturnObject(Integer errno, String errmsg) {
        this.errno = errno;
        this.errmsg = errmsg;
    }

    public InternalReturnObject(Integer errno, String errmsg, T data) {
        this.errno = errno;
        this.errmsg = errmsg;
        this.data=data;
    }
    public InternalReturnObject(T data) {
        this.data=data;
    }

    public InternalReturnObject() {
    }

    public void setErrno(Integer errno) { this.errno = errno;}

    public void setErrmsg(String errmsg) { this.errmsg = errmsg;}

    public void setData(T data) { this.data = data;}

    public Integer getErrno() {
        return errno;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public T getData() {
        return data;
    }
}
