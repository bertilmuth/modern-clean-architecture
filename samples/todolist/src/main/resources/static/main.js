// URL for posting all requests,
// Must be the same as the one in application.properties
// (See https://github.com/bertilmuth/modern-clean-architecture/blob/main/samples/todolist/src/main/resources/application.properties)
const BEHAVIOR_ENDPOINT = "/todolist";

//variables
var todoListUuid;

//selectors
const taskName = document.querySelector('.task_name');
const addTaskButton = document.querySelector('.addTask_button');
const todoList = document.querySelector('.todo_list');
const filterOption = document.querySelector('.filter_todo');

//event listeners
addTaskButton.addEventListener("click", addTask);
todoList.addEventListener("click", deleteOrCompleteTask);
filterOption.addEventListener("click", filterTasks);

// functions
function restoreList(){
	const request = {"@type":"FindOrCreateListRequest"};
	
	post(request, function(response){
		todoListUuid = response.todoListUuid;
		restoreTasksOf(todoListUuid);
	});
}

function restoreTasksOf(todoListUuid) {
	const request = {"@type":"ListTasksRequest", "todoListUuid":todoListUuid};

	post(request, function(response){	
		showTasks(response.tasks);
	});
}

function addTask(event) {	
    event.preventDefault();
	if(taskName.value === ""){
        return null;
    }

	const request = {"@type":"AddTaskRequest", "todoListUuid":todoListUuid, "taskName":taskName.value};

	post(request, function(response){
		const taskUuid = response.taskUuid;
		const taskText = taskName.value;
		
	    createAndAddTask(taskUuid, taskText);
	    taskName.value = "";
	});
}

function deleteOrCompleteTask(event) {
    const item = event.target;
	const taskDiv = item.parentElement;

    if (item.classList[0] === "delete_btn") {
		deleteTask(taskDiv);
    } else if (item.classList[0] === "complete_btn") {
		completeTask(taskDiv);
    }
}

function deleteTask(taskDiv){
	const taskUuid = taskDiv.id;
	const request = {"@type":"DeleteTaskRequest", "todoListUuid":todoListUuid, "taskUuid":taskUuid};

	post(request, function(){
        taskDiv.classList.add("fall");
        taskDiv.addEventListener('transitionend', function () {
            taskDiv.remove();
        })
	});
}

function completeTask(taskDiv){
	const taskUuid = taskDiv.id;
	const request = {"@type":"ToggleTaskCompletionRequest", "todoListUuid":todoListUuid, "taskUuid":taskUuid};

	post(request, function(){
        toggleCompletion(taskDiv);
	});
}

function toggleCompletion(taskDiv){
	taskDiv.classList.toggle("completedTask");
}

function filterTasks(event) {
	const taskDivs = todoList.childNodes;
    for(let i = taskDivs.length-1; i>=0; i-- ){
		const taskDiv = taskDivs[i];
		taskDiv.remove();
	};
	
    switch (event.target.value) {
        case "all":
			restoreTasksOf(todoListUuid);
            break;
        case "completed":
			filterTasksByCompletion(true);
            break;
        case "uncompleted":
			filterTasksByCompletion(false);
            break;
    }
} 

function filterTasksByCompletion(c){
	const request = {"@type":"FilterTasksRequest", "todoListUuid":todoListUuid, completed:c};
	post(request, function(response){	
		showTasks(response.tasks);
	});
}


function showTasks(tasks){
	tasks.forEach(task => {
		createAndAddTask(task.uuid, task.name, task.completed);
	});
}

function createAndAddTask(taskUuid, taskText, completed){
    const taskDiv = document.createElement('div');
    taskDiv.classList.add('todo');
	taskDiv.id = taskUuid;

    const newTask = document.createElement('li');
    newTask.innerText = taskText;
    newTask.classList.add('taskName_item');
    taskDiv.appendChild(newTask)

    const completedButton = document.createElement('button');
    completedButton.innerHTML = '<i class="fas fa-check"></i>';
    completedButton.classList.add('complete_btn');
    taskDiv.appendChild(completedButton);

    const deleteButton = document.createElement('button');
    deleteButton.innerHTML = '<i class="fas fa-trash"></i>';
    deleteButton.classList.add('delete_btn');
    taskDiv.appendChild(deleteButton);

	if(completed){
		toggleCompletion(taskDiv);
	}
	todoList.appendChild(taskDiv);
}


function post(jsonObject, responseHandler) {	
	const xhr = new XMLHttpRequest();
	xhr.open("POST", BEHAVIOR_ENDPOINT);

	xhr.setRequestHeader("Accept", "application/json");
	xhr.setRequestHeader("Content-Type", "application/json");
	
	xhr.onreadystatechange = function() {
		if (xhr.readyState === 4) {
			response = xhr.responseText.length > 0? JSON.parse(xhr.response) : "";
			if(response.error){
				alert('Status ' + response.status + ' "' + response.message + '"');
			} else{
				responseHandler(response);
			}
		}	
	};
	
	const jsonString = JSON.stringify(jsonObject);
	xhr.send(jsonString);
}