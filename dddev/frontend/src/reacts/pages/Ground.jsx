import { useState, useEffect } from 'react';
import { useSelector, useDispatch } from 'react-redux';
import { useNavigate } from 'react-router-dom';
import eetch from 'eetch/eetch';
import { setMenu } from 'redux/actions/menu';
import { setMessage } from 'redux/actions/menu';
import { logoutUser } from 'redux/actions/user';
import { updateUser } from 'redux/actions/user';

import SelectTransparent from 'reacts/pages/components/common/SelectTransparent';

import AddHomeIcon from '@mui/icons-material/AddHome';
import GitHubIcon from '@mui/icons-material/GitHub';
import * as s from 'reacts/styles/components/intro/GroundInit';
const GroundInit = () => {
  const dispatch = useDispatch();
  const navigate = useNavigate();
  const user = useSelector((state) => state.user);
  const [repositories, setRepositories] = useState([]);
  const [repository, setRepository] = useState(null);

  const createGround = () => {
    eetch
      .createGround({ accessToken: user.accessToken, refreshToken: user.refreshToken, name: repository.name, repoId: repository.repoId })
      .then((res) => {
        dispatch(updateUser({ lastGround: res.data.id }));
        eetch
          .userGrounds({ accessToken: user.accessToken, refreshToken: user.refreshToken })
          .then((grounds) => {
            const groundsList = grounds.data.map((ground) => ground.ground.id);
            const groundsMap = grounds.data.map((ground) => ground.ground);
            const groundsMine = grounds.data.filter((ground) => ground.isOwner === true).map((ground) => ground.ground);
            dispatch(
              updateUser({
                groundsList,
                groundsMap,
                groundsMine,
              }),
            );
            navigate(`/${res.data.id}`);
          })
          .catch((err) => {
            if (err.message === 'RefreshTokenExpired') {
              dispatch(logoutUser());
              dispatch(setMenu(false));
              dispatch(setMessage(false));
              navigate(`/login`);
            }
          });
      })
      .catch((err) => {
        if (err.message === 'RefreshTokenExpired') {
          dispatch(logoutUser());
          dispatch(setMenu(false));
          dispatch(setMessage(false));
          navigate(`/login`);
        }
      });
  };

  useEffect(() => {
    eetch
      .repoList({ accessToken: user.accessToken, refreshToken: user.refreshToken })
      .then((res) => {
        const noGrounds = res.data.filter((repo) => !repo.isGround);
        setRepositories(noGrounds);
        setRepository(noGrounds[0]);
      })
      .catch((err) => {
        if (err.message === 'RefreshTokenExpired') {
          dispatch(logoutUser());
          dispatch(setMenu(false));
          dispatch(setMessage(false));
          navigate(`/login`);
        }
      });
  }, [user.accessToken, user.refreshToken]);

  return (
    <s.GroundWrapper>
      {repositories.length > 0 ? (
        <>
          <s.Title>새로운 그라운드 생성</s.Title>
          <SelectTransparent label="리포지터리" list={repositories} select={setRepository} selected={repository.name} />
          <s.TextButton onClick={createGround}>
            시작하기
            <AddHomeIcon />
          </s.TextButton>
        </>
      ) : (
        <>
          <s.Title>
            등록 가능한 <br />
            리포지터리가 없어요.
          </s.Title>
          <s.TextLink href="https://github.com/">
            만들러 가기
            <GitHubIcon />
          </s.TextLink>
        </>
      )}
    </s.GroundWrapper>
  );
};

export default GroundInit;
