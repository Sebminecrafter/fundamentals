const quotes = [
    "Testing, testing, 1, 2, 3",
    "Fundamentals > Essentials",
    "\"No, I don't think I've added that to Fundamentals yet.\" - Sebminecrafter",
    "\"To-do, more like to-delete! ... wait, is this recording?\" - Sebminecrafter",
    "Subscribe to Sebminecrafter",
    "Should I add it?",
    "Will it ever be found?"
];

document.querySelectorAll(".fundamentals-quote").forEach(elem => {
    elem.textContent = quotes[Math.floor(Math.random() * quotes.length)]
});
