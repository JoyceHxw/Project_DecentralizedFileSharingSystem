import request from '@/utils/request'

export const teamGetListService = (userId, teamName, isMy) =>
  request.get('/team/find', {
    params: {
      userId,
      teamName,
      isMy
    }
  })

export const teamJoinService = ({ userId, teamId, password }) =>
  request.post('team/join', { userId, teamId, password })

export const teamQuitService = (userId, teamId) =>
  request.post('/team/quit', null, {
    params: {
      userId,
      teamId
    }
  })

export const teamUpdateService = ({
  userId,
  teamId,
  teamName,
  teamPassword,
  description
}) =>
  request.post('team/update', {
    userId,
    teamId,
    teamName,
    teamPassword,
    description
  })

export const teamCreateService = ({
  teamName,
  userId,
  teamPassword,
  description
}) =>
  request.post('team/create', { teamName, userId, teamPassword, description })
